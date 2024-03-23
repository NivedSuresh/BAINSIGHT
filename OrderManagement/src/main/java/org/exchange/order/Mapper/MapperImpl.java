package org.exchange.order.Mapper;


import lombok.RequiredArgsConstructor;
import org.exchange.library.Dto.Order.OrderDto;
import org.exchange.order.Model.Order;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

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
