package com.bainsight.risk.Message.gRPC_Client;

import com.bainsight.risk.Debug.Debugger;
import com.bainsight.risk.Exception.*;
import com.bainsight.risk.Mapper.Mapper;
import com.bainsight.risk.Model.Entity.CandleStick;
import com.bainsight.risk.Model.Entity.DailyOrderMeta;
import com.bainsight.risk.repo.CandleStickRepo;
import com.bainsight.risk.repo.DailyOrderMetaRepo;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.server.service.GrpcService;
import org.bainsight.*;
import org.exchange.library.Exception.GlobalException;
import org.exchange.library.Exception.IO.ServiceUnavailableException;
import org.exchange.library.Exception.NotFound.SymbolNotFoundException;
import org.exchange.library.Exception.Order.RiskCheckFailureException;
import org.exchange.library.KafkaEvent.RollbackEvent;
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
public class RiskManagementService extends RiskManagementGrpc.RiskManagementImplBase {


    private final CandleStickRepo stickRepo;
    private final DailyOrderMetaRepo dailyOrderMetaRepo;
    private final RedisTemplate<String, Object> redisTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Mapper mapper;
    private final Debugger DEBUGGER;

    @GrpcClient("portfolio-service")
    PortfolioValidateGrpc.PortfolioValidateBlockingStub portfolioClient;

    @Value("${allowed.open.orders}")
    private Integer allowedOpenOrders;

    @Value("${allowed.spend.amount}")
    private Double spendable;

    @Value("${allowed.quantity.per.bid}")
    private Long maximumQuantityPerBid;



    @Override
    public void checkIfProceedable(RiskRequest request, StreamObserver<Proceedable> responseObserver) {
        try{
            DEBUGGER.DEBUG(log, "PROCESSING ORDER VALIDATION");
            Proceedable proceedable = validateOrder(request);
            DEBUGGER.DEBUG(log, "PROCEEDABLE: {}", proceedable);
            responseObserver.onNext(proceedable);
        }
        catch (GlobalException e){
            DEBUGGER.DEBUG(log, "EXCEPTION OCCURED WHILE PROCESSING ORDER. EX: {}", e.getMessage());
            responseObserver.onError((Status.OUT_OF_RANGE.withDescription(e.getMessage())).asRuntimeException());
            return;
        }
        DEBUGGER.DEBUG(log, "FINISHED ORDER PROCESSING");
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
        boolean orderMetaProcessed = false;
        boolean portfolioProcessed = false;
        boolean lock = false;

        try{

            CandleStick candleStick = fetchStick(request);


            if(candleStick == null) throw new SymbolNotFoundException();
            DEBUGGER.DEBUG(log, "Fetched candlestick: {}", candleStick);


            DEBUGGER.DEBUG(log, "Attempting to gain lock!");
            lock = this.lock(request.getUcc());
            DEBUGGER.DEBUG(log, "Locked: {}", lock);
            if(!lock) throw new FailedToAcquireLockException();


            if(isLimit) this.validatePriceBoundaries(candleStick, request.getPrice());
            else request = request.toBuilder().setPrice(candleStick.getHigh()).build();

            DailyOrderMeta dailyOrderMeta = this.validateSEBIRules(request, isBID);
            orderMetaProcessed = true;
            DEBUGGER.DEBUG(log, "Finished updating daily order meta!");


            Proceedable proceedable = this.validatePortfolio(request);
            DEBUGGER.DEBUG(log, "Proceedable after portfolio validation Proceedable:- {} - MSG:- {}", proceedable.getProceedable(), proceedable.getMessage());
            if(!proceedable.getProceedable()) {
                throw new RiskCheckFailureException(proceedable.getMessage());
            }
            portfolioProcessed = true;
            DEBUGGER.DEBUG(log, "Portfolio validated!");


            /* DON'T RETRY TO UNLOCK IF UNLOCKING FAILED, THE LOCK WILL AUTOMATICALLY EXPIRE IN A MINUTE */
           lock = false;
           this.unlock(request.getUcc());
           DEBUGGER.DEBUG(log, "Unlocked!");

           return Proceedable.newBuilder().setProceedable(true).setMessage("").build();
        }
        catch (RuntimeException ex){

            DEBUGGER.DEBUG(log, "Exception: {}", ex.getMessage());

            if(orderMetaProcessed && portfolioProcessed) return Proceedable.newBuilder().setProceedable(true).setMessage("").build();

            if(orderMetaProcessed) this.rollBackDailyOrderMeta(this.mapper.getRollbackEvent(request));
            if(lock) this.unlock(request.getUcc());

            if(ex instanceof GlobalException) throw ex;
            if(ex instanceof StatusRuntimeException sr && (sr.getStatus() != Status.UNAVAILABLE ||
                                                           sr.getStatus() != Status.UNKNOWN)){
                throw sr;
            }
            else throw new ServiceUnavailableException();
        }
    }

    public void portfolioValidationRollback(RollbackEvent request) {
        try{ this.kafkaTemplate.send("portfolio-validation-rollback", request); }
        catch (RuntimeException e){
            /* TODO: IMPLEMENT JOURNALING */
        }
    }

    private boolean lock(String ucc) {
        try{
            return Boolean.TRUE.equals(this.redisTemplate.opsForValue().setIfAbsent("Processing:".concat(ucc), true, 60, TimeUnit.SECONDS));
        }
        catch (RuntimeException e){
            log.error("Exception occurred while updating lock {}", e.getMessage());
            throw new FailedToAcquireLockException("The service is not available at the moment!");
        }
    }

    private void unlock(String ucc){
        try{ this.redisTemplate.delete("Processing:".concat(ucc)); }
        catch (JedisException e){
            log.error("An exception occurred while unlocking: {}", e.getMessage());
            throw new FailedToUnlockException();
        }
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

        DailyOrderMeta dailyOrderMeta = fetchDailyOrderMeta(request.getUcc());


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

        DEBUGGER.DEBUG(log, "DailyOrderMeta has been manipulated: {}", dailyOrderMeta);
        this.updateDailyOrderMeta(dailyOrderMeta);

        return dailyOrderMeta;
    }


    private DailyOrderMeta fetchDailyOrderMeta(String ucc){
        Optional<DailyOrderMeta> orderMetaOptional = this.dailyOrderMetaRepo.findById(ucc);
        DailyOrderMeta dailyOrderMeta = orderMetaOptional.orElseGet(() -> new DailyOrderMeta(ucc, 0, 0.0));
        DEBUGGER.DEBUG(log, "Fetched DailyOrderMeta: {}", dailyOrderMeta);
        return dailyOrderMeta;
    }

    public void rollBackDailyOrderMeta(RollbackEvent decreasable){
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


            if(decreasable.getTransactionType() == org.exchange.library.Enums.TransactionType.BID){
                double newTotalAmountSpent = dailyOrderMeta.getTotalAmountSpent() - decreasable.getPrice();
                if(newTotalAmountSpent < 0)
                {
                    /* TODO: IMPLEMENT JOURNALING */
                    log.error("New total amount spent for the UCC {} is  : {}", decreasable.getUcc(), newTotalAmountSpent);
                    return;
                }
                dailyOrderMeta.setTotalAmountSpent(newTotalAmountSpent);
            }

            this.dailyOrderMetaRepo.save(dailyOrderMeta);
        }
        catch (JedisException j){
            /* MESSAGES ARE BEING PROCESSED BY A SINGLE KAFKA CONSUMER THREAD IF DB IS NOT
            *  AVAILABLE, THEN SLOW DOWN THE PROCESS AND RE-ADD THE MESSAGE BACK TO THE STREAM. */
            this.addMessageBackToStream(decreasable);
            try{ Thread.sleep(10000); }
            catch (InterruptedException ignored){
                /* TODO: IMPLEMENT JOURNALING */
            }
        }
    }

    private void addMessageBackToStream(RollbackEvent decreasable) {
        try{
            this.kafkaTemplate.send("daily-order-meta-decrease-event", decreasable);
        }catch (KafkaException k){
            /* TODO: IMPLEMENT JOURNALING */
            log.error("Failed to add DailyOrderMetaEvent back to stream: {}", decreasable);
        }
    }


    public Proceedable validatePortfolio(RiskRequest risk) {
        try{
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
                        .setOrderType(risk.getOrderType())
                        .setBalanceRequired(risk.getPrice() * risk.getQuantity())
                        .build();

                proceedable = this.portfolioClient.checkIfBidValid(validateBid);
            }

            return proceedable;
        }
        catch (StatusRuntimeException ex){
            return Proceedable.newBuilder().setMessage("Service Unavailable!").setProceedable(false).build();
        }

    }

}
