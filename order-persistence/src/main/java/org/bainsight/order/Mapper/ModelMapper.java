package org.bainsight.order.Mapper;


import org.bainsight.GrpcOrderRequest;
import org.bainsight.order.Model.Entity.Match;
import org.bainsight.order.Model.Entity.Order;
import org.bainsight.order.Model.Events.OrderMatch;
import org.exchange.library.Enums.OrderStatus;
import org.exchange.library.Enums.OrderType;
import org.exchange.library.Enums.TransactionType;
import org.exchange.library.Exception.BadRequest.InvalidStateException;
import org.exchange.library.KafkaEvent.PortfolioUpdateEvent;
import org.exchange.library.KafkaEvent.WalletUpdateEvent;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ModelMapper {



    public Match getMatch(OrderMatch orderMatch, boolean isValidated){
        return Match.builder()
                .matchedQuantity(orderMatch.matchedQuantity())
                .priceMatchedFor(orderMatch.priceMatchedFor())
                .matchTime(orderMatch.matchTime())
                .isValidated(isValidated)
                .orderId(UUID.fromString(orderMatch.orderId()))
                .build();
    }

    public Order getOrder(GrpcOrderRequest request) {
        return Order.builder()
                .ucc(UUID.fromString(request.getUcc()))
                .symbol(request.getSymbol())
                .exchange(request.getExchange())
                .orderType(this.getOrderType(request.getOrderType()))
                .transactionType(this.getTransactionType(request.getTransactionType()))
                .price(request.getPrice())
                .quantityRequested(request.getQuantity())
                .quantityMatched(0L)
                .orderStatus(OrderStatus.OPEN.name())
                .orderPlacedAt(LocalDateTime.now())
                .version(1L)
                .build();
    }


    public org.bainsight.OrderType getOrderType(OrderType orderType) {
        switch (orderType){
            case ORDER_TYPE_LIMIT -> { return org.bainsight.OrderType.ORDER_TYPE_LIMIT; }
            case ORDER_TYPE_MARKET -> { return org.bainsight.OrderType.ORDER_TYPE_MARKET; }
            default -> throw new InvalidStateException();
        }
    }

    public org.bainsight.TransactionType getTransactionType(TransactionType transactionType) {
        switch (transactionType){
            case BID -> { return org.bainsight.TransactionType.BID; }
            case ASK -> { return org.bainsight.TransactionType.ASK; }
            default -> throw new InvalidStateException();
        }
    }

    public TransactionType getTransactionType(org.bainsight.TransactionType transactionType) {
        switch (transactionType){
            case BID -> {return TransactionType.BID;}
            case ASK -> {return TransactionType.ASK;}
            default -> throw new InvalidStateException();
        }
    }

    public OrderType getOrderType(org.bainsight.OrderType orderType) {
        switch (orderType){
            case ORDER_TYPE_MARKET -> { return OrderType.ORDER_TYPE_MARKET; }
            case ORDER_TYPE_LIMIT -> { return OrderType.ORDER_TYPE_LIMIT; }
            default -> throw new InvalidStateException();
        }
    }

    public WalletUpdateEvent getWalletValidation(final Match match, final UUID ucc, String symbol) {
        return WalletUpdateEvent.builder()
                .matchId(match.getMatchId())
                .quantity(match.getMatchedQuantity())
                .requiredBalance(match.getMatchedQuantity() * match.getPriceMatchedFor())
                .ucc(ucc)
                .symbol(symbol)
                .build();
    }

    public PortfolioUpdateEvent getPortfolioUpdateEvent(Match match, Order order) {
        return PortfolioUpdateEvent.builder()
                .isBid(order.getTransactionType() == TransactionType.BID)
                .symbol(order.getSymbol())
                .quantity(match.getMatchedQuantity())
                .ucc(order.getUcc())
                .pricePerShare(match.getPriceMatchedFor())
                .build();
    }
}
