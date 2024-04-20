package com.bainsight.risk.Message.gRPC_Client;

import com.bainsight.risk.Exception.*;
import com.bainsight.risk.Model.Entity.CandleStick;
import com.bainsight.risk.repo.CandleStickRepo;
import com.bainsight.risk.repo.DailyOrderMetaRepo;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.server.service.GrpcService;
import org.bainsight.*;
import com.bainsight.risk.Model.Entity.DailyOrderMeta;
import org.exchange.library.Exception.GlobalException;
import org.exchange.library.Exception.NotFound.SymbolNotFoundException;
import org.exchange.library.KafkaEvent.DailyOrderMetaEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import redis.clients.jedis.exceptions.JedisException;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@GrpcService
@Slf4j
@RequiredArgsConstructor
public class RiskManagementService extends RiskServiceGrpc.RiskServiceImplBase {


    private final CandleStickRepo stickRepo;
    private final DailyOrderMetaRepo dailyOrderMetaRepo;
    private final RedisTemplate<String, Object> redisTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @GrpcClient("portfolio")
    private PortfolioValidateGrpc.PortfolioValidateBlockingStub portfolioClient;

    @Value("${allowed.open.orders}")
    private Integer allowedOpenOrders;

    @Value("${allowed.spend.amount}")
    private Double spendable;

    @Value("${allowed.quantity.per.bid}")
    private Long maximumQuantityPerBid;



    @Override
    public void checkIfProceedable(RiskRequest request, StreamObserver<Proceedable> responseObserver) {
        try{
            responseObserver.onNext(validateOrder(request));
        }
        catch (GlobalException e){
            responseObserver.onError((Status.OUT_OF_RANGE.withDescription(e.getMessage())).asRuntimeException());
            return;
        }
        responseObserver.onCompleted();
    }



    /**
     * <p>
     * CHECK IF USER IS TRYING TO BUY THE SYMBOL FOR THE ALLOWED PRICE RANGE.
     * <br>
     * CHECK IF THE AMOUNT OF SHARES THE USER IS TRYING TO BUY IN A SINGLE ORDER FALLS UNDER THE LIMIT.
     * <br>
     * ENSURE THE USER DOES NOT HAVE A LOT OF OPEN ORDERS PENDING.
     * */
    private Proceedable validateOrder(RiskRequest request) {

        final boolean isBID = TransactionType.BID == request.getTransactionType();
        final boolean isLimit = request.getOrderType() == OrderType.ORDER_TYPE_LIMIT;

        CandleStick candleStick = null;

        if(isBID) candleStick = fetchStick(request);

        Proceedable proceedable = this.validatePortfolio(request, candleStick);

        if(isLimit){
            candleStick = candleStick == null ? fetchStick(request) : candleStick;
            this.validatePriceBoundaries(candleStick, request.getPrice());
        }

        Boolean lock = this.lock(request.getUcc());
        if(lock == null || !lock){
            throw new FailedToAcquireLockException();
        }

        DailyOrderMeta dailyOrderMeta = this.validateSEBIRules(request, isBID);

        try
        {
            this.updateDailyOrderMeta(dailyOrderMeta);
        }
        catch (GlobalException e)
        {
            this.unlock(request.getUcc());
            throw e;
        }
        this.unlock(request.getUcc());

        return Proceedable.newBuilder().setProceedable(true).build();
    }

    private Boolean lock(String ucc) {
        try{
            return this.redisTemplate.opsForValue().setIfAbsent("Processing:".concat(ucc), true, 60, TimeUnit.SECONDS);
        }
        catch (RuntimeException e){
            log.error("Exception occurred while updating lock {}", e.getMessage());
            throw new FailedToAcquireLockException("The service is not available at the moment!");
        }
    }

    private void unlock(String ucc){
        try{ this.redisTemplate.delete("Processing:".concat(ucc)); }
        catch (JedisException e){ log.error("An exception occurred while unlocking: {}", e.getMessage()); }
    }


    private void validatePriceBoundaries(final CandleStick candleStick, final double price) {
        double lowestAcceptable = Math.round(candleStick.getLow() * 0.8);
        double highestAcceptable = Math.round(candleStick.getHigh() * 1.2);

        if(price < lowestAcceptable)
        {
            throw new PriceBelowLowestAcceptableException(lowestAcceptable, candleStick.getSymbol());
        }
        else if (price > highestAcceptable)
        {
            throw new PriceAboveHighestAcceptableException(highestAcceptable, candleStick.getSymbol());
        }

    }

    private CandleStick fetchStick(RiskRequest request) {
        return this.stickRepo.findById(request.getSymbol()).orElseThrow(SymbolNotFoundException::new);
    }

    private void updateDailyOrderMeta(DailyOrderMeta dailyOrderMeta) {
        this.dailyOrderMetaRepo.save(dailyOrderMeta);
    }

    private DailyOrderMeta validateSEBIRules(RiskRequest request, boolean isBID) {


        if(isBID && request.getQuantity() > maximumQuantityPerBid) {
            throw new ExceededPurchaseQuantityPerRequestException(maximumQuantityPerBid);
        }

        Optional<DailyOrderMeta> orderMetaOptional = this.dailyOrderMetaRepo.findById(request.getUcc());

        DailyOrderMeta dailyOrderMeta = orderMetaOptional.orElseGet(() -> new DailyOrderMeta(request.getUcc(), 0, 0.0));



        if(dailyOrderMeta.getOpenOrderCount() >= allowedOpenOrders){
            throw new OpenOrderCountExceededException();
        }

        double total = (request.getPrice() * request.getQuantity()) + dailyOrderMeta.getTotalAmountSpent();
        if(isBID && total > spendable){
            double exceededAmount = total - spendable;
            throw new SpendLimitPerDayExceededException(exceededAmount, spendable);
        }


        if(isBID) dailyOrderMeta.setTotalAmountSpent(total);
        dailyOrderMeta.setOpenOrderCount(dailyOrderMeta.getOpenOrderCount() + 1);

        return dailyOrderMeta;
    }


    public void decreaseDailyOrderMeta(DailyOrderMetaEvent decreasable){
        try{
            Optional<DailyOrderMeta> optional = this.dailyOrderMetaRepo.findById(decreasable.getUcc());
            if (optional.isEmpty()) {
                /* TODO: IMPLEMENT JOURNALING */
                log.error("Failed to find DailyOrderMeta with the UCC: {}", decreasable.getUcc());
                return;
            }
            DailyOrderMeta dailyOrderMeta = optional.get();
            int newOpenCount = dailyOrderMeta.getOpenOrderCount() - 1;

            if(newOpenCount < 0)
            {
                /* TODO: IMPLEMENT JOURNALING */
                log.error("Open Order Count for the UCC {} is less than 0 -> {}", decreasable.getUcc(), newOpenCount);
                return;
            }
            dailyOrderMeta.setOpenOrderCount(newOpenCount);


            double newTotalAmountSpent = dailyOrderMeta.getTotalAmountSpent() - decreasable.getTotalAmountSpent();
            if(newTotalAmountSpent < 0)
            {
                /* TODO: IMPLEMENT JOURNALING */
                log.error("Open Order Count for the UCC {} is  : {}", decreasable.getUcc(), newOpenCount);
                return;
            }
            dailyOrderMeta.setTotalAmountSpent(newTotalAmountSpent);


            this.dailyOrderMetaRepo.save(dailyOrderMeta);
        }
        catch (JedisException j){
            /* MESSAGES ARE BEING PROCESSED BY A SINGLE KAFKA CONSUMER THREAD IF DB IS NOT
            *  AVAILABLE, THEN SLOW DOWN THE PROCESS AND RE-ADD THE MESSAGE BACK TO THE STREAM. */
            this.addMessageBackToStream(decreasable);
            try{ Thread.sleep(10000); }
            catch (InterruptedException e){}
        }
    }

    private void addMessageBackToStream(DailyOrderMetaEvent decreasable) {
        try{
            this.kafkaTemplate.send("daily-order-meta-decrease-event", decreasable);
        }catch (KafkaException k){
            /* TODO: IMPLEMENT JOURNALING */
            log.error("Failed to add DailyOrderMetaEvent back to stream: {}", decreasable);
        }
    }


    public Proceedable validatePortfolio(RiskRequest risk, CandleStick candleStick) {
        Proceedable proceedable;

        if(risk.getTransactionType() == TransactionType.ASK)
        {
            ValidateAsk validateAsk = ValidateAsk.newBuilder()
                    .setUcc(risk.getUcc())
                    .setSymbol(risk.getSymbol())
                    .setQuantityRequired(risk.getQuantity())
                    .build();

            proceedable = this.portfolioClient.checkIfAskValid(validateAsk);
        }
        else
        {
            ValidateBid validateBid = ValidateBid.newBuilder()
                    .setUcc(risk.getUcc())
                    .setBalanceRequired(candleStick.getHigh() * risk.getQuantity())
                    .build();
            proceedable = this.portfolioClient.checkIfBidValid(validateBid);
        }

        return proceedable;

    }

}
