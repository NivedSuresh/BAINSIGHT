package org.exchange.order.Mapper;


import org.exchange.library.Dto.Order.OrderDto;
import org.exchange.library.Dto.Order.OrderRequest;
import org.exchange.library.Dto.Order.OrderResponse;
import org.exchange.library.Enums.OrderStatus;
import org.exchange.library.KafkaEvent.OrderAcceptedEvent;
import org.exchange.order.Model.Order;

public interface Mapper {

    OrderResponse entityToOrderResponse(Order order);

    Order requestToOrderEntity(OrderRequest request, OrderStatus status, Long matchedSize, String broker);

    OrderAcceptedEvent entityToOrderCreatedEvent(Order order);

    OrderDto entityToOrderDto(Order order);

    Order verifiedEventToOrderEntity(OrderVerifiedEvent verifiedEvent);
}
