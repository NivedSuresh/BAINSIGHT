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
    public OrderDto entityToOrderDto(Order order) {
        OrderDto orderDto = new OrderDto();
        BeanUtils.copyProperties(order, orderDto);
        return orderDto;
    }



}
