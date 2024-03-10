package org.exchange.order.Mapper;


import org.exchange.library.Dto.Order.OrderDto;
import org.exchange.library.Dto.Order.OrderRequest;
import org.exchange.library.Dto.Order.OrderResponse;
import org.exchange.library.Enums.OrderStatus;
import org.exchange.library.KafkaEvent.OrderAcceptedEvent;
import org.exchange.order.Model.Order;

public interface Mapper {

    OrderDto entityToOrderDto(Order order);
}
