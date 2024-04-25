package org.bainsight.processing.Service;


import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.bainsight.*;
import org.bainsight.processing.Exception.FailedToCompleteOrderException;
import org.bainsight.processing.Exception.FailedToUpdateOrderException;
import org.bainsight.processing.Mapper.Mapper;
import org.bainsight.processing.Model.Dto.OrderRequest;
import org.exchange.library.Enums.OrderStatus;
import org.exchange.library.Exception.GlobalException;
import org.exchange.library.Exception.IO.ServiceUnavailableException;
import org.exchange.library.Exception.Order.RiskCheckFailureException;
import org.exchange.library.KafkaEvent.RollbackEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
@Slf4j
public class OrderProcessingService {

    private final RiskManagementGrpc.RiskManagementBlockingStub riskClient;
    private final PersistOrderGrpc.PersistOrderBlockingStub persistClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Mapper mapper;


    public OrderProcessingService(@GrpcClient("risk-management") final RiskManagementGrpc.RiskManagementBlockingStub riskClient,
                                  @GrpcClient("order-persistence") final PersistOrderGrpc.PersistOrderBlockingStub persistClient,
                                  final KafkaTemplate<String, Object> kafkaTemplate, final Mapper mapper) {
        this.riskClient = riskClient;
        this.persistClient = persistClient;
        this.kafkaTemplate = kafkaTemplate;
        this.mapper = mapper;
    }


    public void checkIfRiskFreeElseThrow(RiskRequest request){
        try{
            Proceedable proceedable = this.riskClient.checkIfProceedable(request);
            if(!proceedable.getProceedable()) throw new RiskCheckFailureException(proceedable.getMessage());
            log.info("Done validating portfolio!");
        }
        catch (RuntimeException ex){

            if(ex instanceof GlobalException) throw ex;

            if(ex instanceof StatusRuntimeException sre) this.handleGRPCException(sre);

            throw new ServiceUnavailableException();
        }
    }



    public void placeOrder(OrderRequest request, String ucc){
        log.info("Placing order! UCC: {}", ucc);
        final GrpcOrderRequest orderRequest = this.mapper.getGrpcOrderRequest(request, ucc);
        String orderId = null;
        try
        {
            OrderUID orderUID = this.persistClient.persistOrder(orderRequest);
            orderId = orderUID.getOrderId();

            this.sendOrderToExchange(orderRequest, orderId);
        }
        catch (RuntimeException e){
            RollbackEvent rollbackEvent = this.mapper.getRollbackEvent(request, ucc);
            log.info("rolling back!");
            this.rollBackValidation(rollbackEvent);

            if(orderId != null) {
                log.info("Order was persisted, now failing!");
                this.rollbackOrderPersistence(orderId, ucc, e.getMessage());
            }

            if(e instanceof StatusRuntimeException sre) this.handleGRPCException(sre);
            throw new FailedToCompleteOrderException(e.getMessage());
        }
    }

    private void sendOrderToExchange(GrpcOrderRequest orderRequest, String orderId) {
//        throw new RuntimeException("");
    }

    private void updateOrderStatus(UpdateStatusRequest updateStatusRequest){
        try
        {
            log.info("Updating order status!");
            Proceedable proceedable = this.persistClient.updateOrderStatus(updateStatusRequest);
            if(!proceedable.getProceedable()) throw new FailedToUpdateOrderException();
            log.info("Proceedable: {}", proceedable);
        }
        catch (RuntimeException e){
            log.error(e.getMessage());
            if(e instanceof GlobalException) throw e;
            if(e instanceof StatusRuntimeException sre) this.handleGRPCException(sre);
            throw new ServiceUnavailableException();
        }
    }

    private void rollBackValidation(RollbackEvent request) {
        try{
            this.kafkaTemplate.send("risk-rollback", request);
        }
        catch (RuntimeException ex){
            log.error("Exception: {}", ex.getMessage());
        }
    }

    private void rollbackOrderPersistence(String orderId, String ucc, String message){
        UpdateStatusRequest request = null;
        try{
            request = this.mapper.getUpdateStatusRequest(orderId, ucc, OrderStatus.FAILED);
            this.updateOrderStatus(request);
        }
        catch (RuntimeException ex)
        {
            log.error(ex.getMessage());
            this.kafkaTemplate.send("update-order-status", request);
        }
    }


    private void handleGRPCException(StatusRuntimeException ex){
        if(ex.getStatus() == Status.UNAVAILABLE || ex.getStatus() == Status.UNKNOWN) throw new ServiceUnavailableException();
        throw new RiskCheckFailureException(ex.getMessage());
    }

    public void cancelOrder(String ucc, String orderId) {
        try
        {
            UpdateStatusRequest request = this.mapper.getUpdateStatusRequest(orderId, ucc, OrderStatus.CANCELLED);
            RiskRequest riskRequest = this.persistClient.cancelOrder(request);

            this.rollBackValidation(this.mapper.getRollbackEvent(riskRequest));
        }
        catch (RuntimeException ex)
        {
            log.error(ex.getMessage());
            if(ex instanceof StatusRuntimeException e) throw new FailedToUpdateOrderException(e.getMessage());
            if(ex instanceof GlobalException) throw ex;
        }
    }
}
