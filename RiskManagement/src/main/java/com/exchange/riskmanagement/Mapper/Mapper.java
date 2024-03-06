package com.exchange.riskmanagement.Mapper;


import com.exchange.riskmanagement.Model.Entity.SymbolMeta;
import org.exchange.library.Dto.Order.OrderRequest;
import org.exchange.library.Dto.Order.RiskOrderResponse;
import org.exchange.library.Dto.Symbol.SymbolRequest;
import org.exchange.library.Dto.Symbol.SymbolResponse;
import org.exchange.library.Enums.OrderStatus;
import org.exchange.library.KafkaEvent.OrderEvent;
import org.springframework.http.HttpStatusCode;

public interface Mapper {
    SymbolMeta toSymbolMeta(SymbolRequest request);

    SymbolResponse toSymbolResponse(SymbolMeta symbolMeta1);


    RiskOrderResponse getRiskOrderResponse(OrderEvent event, HttpStatusCode exchangeOrderStatus);

    OrderEvent requestToOrderEvent(OrderRequest request, String broker, OrderStatus verified);

}
