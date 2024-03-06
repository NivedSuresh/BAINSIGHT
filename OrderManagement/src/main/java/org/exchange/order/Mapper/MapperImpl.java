package org.exchange.order.Mapper;


import lombok.RequiredArgsConstructor;
import org.exchange.library.Dto.Order.OrderDto;
import org.exchange.library.Dto.Order.OrderRequest;
import org.exchange.library.Dto.Order.OrderResponse;
import org.exchange.library.Enums.OrderStatus;
import org.exchange.library.KafkaEvent.OrderAcceptedEvent;
import org.exchange.order.Model.Order;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MapperImpl implements Mapper {


    @Override
    public OrderResponse entityToOrderResponse(Order order) {
        OrderResponse response = new OrderResponse();
        BeanUtils.copyProperties(order, response);
        return response;
    }

    @Override
    public Order requestToOrderEntity(OrderRequest request, OrderStatus status, Long matchedSize, String broker) {
        Order order = new Order();
        BeanUtils.copyProperties(request, order);
        order.setCreated(LocalDateTime.now());
        order.setFilledQuantity(matchedSize);
        order.setStatus(status);
        order.setBroker(broker);
        return order;
    }

    @Override
    public OrderAcceptedEvent entityToOrderCreatedEvent(Order order) {
        OrderAcceptedEvent acceptedEvent = new OrderAcceptedEvent();
        BeanUtils.copyProperties(order, acceptedEvent);
        return acceptedEvent;
    }

    @Override
    public OrderDto entityToOrderDto(Order order) {
        OrderDto orderDto = new OrderDto();
        BeanUtils.copyProperties(order, orderDto);
        return orderDto;
    }

    @Override
    public Order verifiedEventToOrderEntity(OrderVerifiedEvent verifiedEvent) {
        Order order = new Order();
        BeanUtils.copyProperties(verifiedEvent, order);
        order.setStatus(OrderStatus.CREATED);
        order.setCreated(LocalDateTime.now());
        return order;
    }


}
