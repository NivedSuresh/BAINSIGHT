package org.exchange.order.Mapper;


import org.exchange.library.Dto.Order.OrderDto;
import org.exchange.order.Model.Order;

public interface Mapper {

    OrderDto entityToOrderDto(Order order);
}
