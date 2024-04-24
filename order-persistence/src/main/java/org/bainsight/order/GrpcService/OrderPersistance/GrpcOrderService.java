package org.bainsight.order.GrpcService.OrderPersistance;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.bainsight.*;
import org.bainsight.order.Exception.OrderNotFoundException;
import org.bainsight.order.Mapper.ModelMapper;
import org.bainsight.order.Model.Entity.Match;
import org.bainsight.order.Model.Entity.Order;
import org.bainsight.order.Model.Events.OrderMatch;
import org.exchange.library.Enums.OrderStatus;
import org.exchange.library.Enums.OrderType;
import org.exchange.library.Enums.TransactionType;
import org.exchange.library.Exception.GlobalException;
import org.exchange.library.KafkaEvent.PortfolioUpdateEvent;
import org.exchange.library.KafkaEvent.WalletUpdateEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;


@GrpcService
@RequiredArgsConstructor
@Slf4j
public class GrpcOrderService extends PersistOrderGrpc.PersistOrderImplBase {


    private final OrderRepo orderRepo;
    private final List<String> CANCELLABLE_STATUSES = List.of("OPEN");
    private final ExecutorService greenExecutor;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ModelMapper mapper;
    private final MatchRepo matchRepo;


    @Override
    public void persistOrder(GrpcOrderRequest request, StreamObserver<OrderUID> responseObserver) {
        try{
            Order order = this.mapper.getOrder(request);

            order = this.orderRepo.save(order);

            OrderUID orderUID = OrderUID.newBuilder().setOrderId(order.getOrderId().toString()).build();
            responseObserver.onNext(orderUID);
            responseObserver.onCompleted();
        }
        catch (RuntimeException e){
            if(e instanceof GlobalException g) {
                responseObserver.onError(Status.CANCELLED.withDescription(g.getMessage()).asRuntimeException());
            }
            else {
                responseObserver.onError(Status.RESOURCE_EXHAUSTED.withDescription("Service Unavailable!").asRuntimeException());
            }
            responseObserver.onCompleted();
        }
    }


    @Transactional
    @Override
    public void updateOrderStatus(UpdateStatusRequest request, StreamObserver<Proceedable> responseObserver) {
        try{

            UUID orderId = UUID.fromString(request.getOrderId());
            UUID ucc = UUID.fromString(request.getUcc());

            Integer count = this.orderRepo.updateByUccAndOrderIdAndOrderStatusIn(ucc, orderId, CANCELLABLE_STATUSES, request.getOrderStatus());


            if(count == null || count <= 0){
                responseObserver.onError(Status.NOT_FOUND.withDescription("Order not found!").asRuntimeException());
                return;
            }

            Proceedable proceedable = Proceedable.newBuilder()
                    .setProceedable(true)
                    .setMessage("")
                    .build();
            responseObserver.onNext(proceedable);
            responseObserver.onCompleted();
        }
        catch (RuntimeException ex){
            responseObserver.onError(Status.UNAVAILABLE.withDescription(ex.getMessage()).asRuntimeException());
        }
    }



    @Transactional
    @Override
    public void cancelOrder(UpdateStatusRequest request, StreamObserver<RiskRequest> responseObserver) {
        try
        {
            log.info("Order is being cancelled!");
            UUID orderId = UUID.fromString(request.getOrderId());
            UUID ucc = UUID.fromString(request.getUcc());

            Order order = this.orderRepo.findWithLockingByUccAndOrderIdAndOrderStatusIn(ucc, orderId, CANCELLABLE_STATUSES);
            log.info("Order has been fetched: {}", order);

            if(order == null){
                log.error("Order is null!");
                responseObserver.onError(Status.NOT_FOUND.withDescription("Order not found!").asRuntimeException());
                return;
            }

            log.info("Order status is being updated!");
            order.setOrderStatus(request.getOrderStatus());
            order = this.orderRepo.save(order);
            log.info("Order status has been updated!: {}", order);


            long rollbackQuantity = order.getQuantityRequested() - (order.getQuantityMatched() != null ? order.getQuantityMatched() : 0);
            RiskRequest riskRequest = RiskRequest.newBuilder()
                    .setSymbol(order.getSymbol())
                    .setPrice(order.getPrice())
                    .setQuantity(rollbackQuantity)
                    .setTransactionType(this.mapper.getTransactionType(order.getTransactionType()))
                    .setOrderType(this.mapper.getOrderType(order.getOrderType()))
                    .setUcc(order.getUcc().toString())
                    .build();

            log.info("Risk Request has been prepared! \n{}", riskRequest);

            responseObserver.onNext(riskRequest);
            responseObserver.onCompleted();
            log.info("Completed cancellation!");
        }
        catch (RuntimeException e)
        {
            log.error(e.getMessage());
            if(e instanceof ObjectOptimisticLockingFailureException){
                responseObserver.onError(Status.PERMISSION_DENIED.withDescription("The order is currently being modified, try again.").asRuntimeException());
            }
            responseObserver.onError(Status.UNAVAILABLE.withDescription("Service unavailable.").asRuntimeException());
        }
    }




    public List<Order> findAllByOrderTypeAndOrderStatus(org.bainsight.OrderType orderType, OrderStatus orderStatus) {
        return this.orderRepo.findAllByOrderTypeAndOrderStatus(this.mapper.getOrderType(orderType), orderStatus.name());
    }

    @Transactional
    public void processMatch(OrderMatch orderMatch) {
        try
        {
            UUID orderID = UUID.fromString(orderMatch.orderId());
            Optional<Order> optional = this.orderRepo.findWithLockingByOrderId(orderID);

            if(optional.isEmpty()){
                /* TODO: IMPLEMENT JOURNALING */
                log.error("No order found for the ID!");
                return;
            }

            Order order = optional.get();
            Match match = this.mapper.getMatch(orderMatch, getIsValidated(order));
            match = this.matchRepo.save(match);

            /* IF NOT VALIDATED (IE IS MARKET_BID) THEN GO UPDATE WALLET AND RETURN THE EVENT THROUGH KAFKA */
            if(!match.isValidated())
            {
                log.info("Market_Bid match, match will be updated once balance is deducted!");
                WalletUpdateEvent walletUpdateEvent = this.mapper.getWalletValidation(match, order.getUcc(), order.getSymbol());
                this.kafkaTemplate.send("wallet-updation-market-bid", walletUpdateEvent);
                return;
            }

            log.info("The order was not a market bid thus match will be applied!");
            this.updateOrderAfterMatch(order, match);
        }
        catch (RuntimeException ex)
        {
            /* TODO: IMPLEMENT JOURNALING */
            ex.printStackTrace();
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void updateOrderAfterMatch(Order order, Match match) {
        log.info("Updating order after match: {}", order);
        long totalMatched = order.getQuantityMatched() + match.getMatchedQuantity();
        if(totalMatched > order.getQuantityRequested()) {
            /* TODO: IMPLEMENT JOURNALING */
            order.setQuantityMatched(order.getQuantityRequested());
        }
        else order.setQuantityMatched(totalMatched);

        if(totalMatched == order.getQuantityRequested()){
            /* TODO: FIRST GRPC THEN KAFKA */
            log.info("Order has been filled completely!");
            order.setOrderStatus(OrderStatus.CLOSED.name());
            this.kafkaTemplate.send("open-order-count-decrement", order.getUcc());
        }

        this.orderRepo.save(order);

        /* IF WAS VALIDATED BEFORE MATCH THEN GO AND UPDATE PORTFOLIO */
        if(match.isValidated())
        {
            log.info("Match was validated on order placement itself and then never updated, thus portfolio/wallet needs to be updated!");
            PortfolioUpdateEvent portfolioUpdateEvent = this.mapper.getPortfolioUpdateEvent(match, order);
            this.kafkaTemplate.send("update-portfolio", portfolioUpdateEvent);
        }
    }


    /**
     * Returns true for orders except Market_Bid's
     * */
    boolean getIsValidated(Order order){
        return !(order.getTransactionType() == TransactionType.BID &&
                order.getOrderType() == OrderType.ORDER_TYPE_MARKET);
    }

    @Transactional
    public Order findOrderById(UUID orderId) {
        return this.orderRepo.findWithLockingByOrderId(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
    }
}