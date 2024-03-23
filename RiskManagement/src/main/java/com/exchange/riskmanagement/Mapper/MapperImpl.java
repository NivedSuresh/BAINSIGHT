package com.exchange.riskmanagement.Mapper;


import com.exchange.riskmanagement.Model.Entity.SymbolMeta;
import lombok.RequiredArgsConstructor;
import org.exchange.library.Dto.Order.OrderRequest;
import org.exchange.library.Dto.Order.RiskOrderResponse;
import org.exchange.library.Dto.Symbol.SymbolRequest;
import org.exchange.library.Dto.Symbol.SymbolResponse;
import org.exchange.library.Enums.OrderStatus;
import org.exchange.library.KafkaEvent.OrderEvent;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MapperImpl implements Mapper {

    @Override
    public SymbolMeta toSymbolMeta(SymbolRequest request) {
        return SymbolMeta.builder().tradingSymbol(request.getTradingSymbol()).id(request.getId()).build();
    }

    @Override
    public SymbolResponse toSymbolResponse(SymbolMeta symbolMeta) {
        return SymbolResponse.builder().symbol(symbolMeta.getTradingSymbol()).id(symbolMeta.getId()).build();
    }

    @Override
    public RiskOrderResponse getRiskOrderResponse(OrderEvent event, HttpStatusCode exchangeOrderStatus) {
        RiskOrderResponse orderResponse = new RiskOrderResponse();
        BeanUtils.copyProperties(event, orderResponse);
        orderResponse.setExchangeOrderStatus(exchangeOrderStatus.is2xxSuccessful() ? OrderStatus.ACCEPTED : OrderStatus.REJECTED);
        return orderResponse;
    }

    @Override
    public OrderEvent requestToOrderEvent(OrderRequest request, String ucc, OrderStatus status) {
        OrderEvent event = new OrderEvent();
        BeanUtils.copyProperties(request, event);
        event.setOrderId(UUID.randomUUID());
        event.setUcc(ucc);
        event.setStatus(status);
        return event;
    }


}
