package org.bainsight.processing.Mapper;

import org.bainsight.*;
import org.bainsight.processing.Model.Dto.OrderRequest;
import org.exchange.library.Dto.Order.OrderResponse;
import org.exchange.library.Enums.OrderStatus;
import org.exchange.library.KafkaEvent.RollbackEvent;
import org.springframework.stereotype.Service;


@Service
public class Mapper {

    public RiskRequest getRiskRequest(OrderRequest orderRequest, String ucc) {
        return RiskRequest.newBuilder()
                .setQuantity(orderRequest.quantity())
                .setSymbol(orderRequest.symbol())
                .setTransactionType(orderRequest.transactionType())
                .setPrice(orderRequest.price())
                .setOrderType(orderRequest.orderType())
                .setUcc(ucc)
                .build();
    }

    public RollbackEvent getRollbackEvent(OrderRequest request, String ucc) {

        org.exchange.library.Enums.OrderType orderType = request.orderType() == OrderType.ORDER_TYPE_LIMIT ? org.exchange.library.Enums.OrderType.ORDER_TYPE_LIMIT : org.exchange.library.Enums.OrderType.ORDER_TYPE_MARKET;
        org.exchange.library.Enums.TransactionType transactionType = request.transactionType() == TransactionType.BID ? org.exchange.library.Enums.TransactionType.BID : org.exchange.library.Enums.TransactionType.ASK;

        return RollbackEvent.builder()
                .price(request.price())
                .quantity(request.quantity())
                .symbol(request.symbol())
                .orderType(orderType)
                .ucc(ucc)
                .transactionType(transactionType)
                .build();
    }

    public GrpcOrderRequest getGrpcOrderRequest(OrderRequest request, String ucc) {
        return GrpcOrderRequest.newBuilder()
                .setUcc(ucc)
                .setSymbol(request.symbol())
                .setOrderType(request.orderType())
                .setExchange(request.exchange())
                .setPrice(request.orderType() == OrderType.ORDER_TYPE_MARKET ? 0 : request.price())
                .setTransactionType(request.transactionType())
                .setQuantity(request.quantity())
                .build();
    }

    public UpdateStatusRequest getUpdateStatusRequest(String orderId, String ucc,  OrderStatus status) {
        return UpdateStatusRequest.newBuilder()
                .setOrderId(orderId)
                .setUcc(ucc)
                .setOrderStatus(status.name())
                .build();
    }

    public RollbackEvent getRollbackEvent(RiskRequest request) {
        org.exchange.library.Enums.OrderType orderType = request.getOrderType() == OrderType.ORDER_TYPE_LIMIT ? org.exchange.library.Enums.OrderType.ORDER_TYPE_LIMIT : org.exchange.library.Enums.OrderType.ORDER_TYPE_MARKET;
        org.exchange.library.Enums.TransactionType transactionType = request.getTransactionType() == TransactionType.BID ? org.exchange.library.Enums.TransactionType.BID : org.exchange.library.Enums.TransactionType.ASK;

        return RollbackEvent.builder()
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .symbol(request.getSymbol())
                .orderType(orderType)
                .ucc(request.getUcc())
                .transactionType(transactionType)
                .build();
    }
}
