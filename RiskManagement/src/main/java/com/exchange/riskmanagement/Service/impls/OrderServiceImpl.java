package com.exchange.riskmanagement.Service.impls;

import com.exchange.riskmanagement.Mapper.Mapper;
import com.exchange.riskmanagement.Model.Properties.ExchangeOrderURI;
import com.exchange.riskmanagement.Repository.ClientMetaRepo;
import com.exchange.riskmanagement.Repository.ClientSecurityRepo;
import com.exchange.riskmanagement.Service.KafkaService;
import com.exchange.riskmanagement.Service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.NotImplementedException;
import org.exchange.library.Dto.Order.OrderRequest;
import org.exchange.library.Dto.Order.RiskOrderResponse;
import org.exchange.library.Enums.OrderStatus;
import org.exchange.library.Exception.Order.*;
import org.exchange.library.KafkaEvent.OrderEvent;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@EnableCaching
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final WebClient webClient;
    private final ClientSecurityRepo clientSecurityRepo;
    private final Mapper mapper;
    private final KafkaService kafkaService;
    private final ClientMetaRepo metaRepo;
    private final ExchangeOrderURI URI;

    /*
        - validate by comparing the request with user holdings/balance.
    */
    @Override
    @Transactional(readOnly = true)
    public Mono<? extends RiskOrderResponse> createOrder(OrderRequest request, String ucc) {
        switch (request.transactionType()){
            case ASK -> {
                return checkIfAskRequestValid(request, ucc)
                        .handle((valid, sink) -> {
                            if(!valid) sink.error(new NotEnoughSecurityToPlaceOrder());
                        })
                        .then(proceedWithValidatedRequest(request, ucc));
            }
            case BID -> {
                return checkIfBidRequestValid(request, ucc)
                        .handle((valid, sink) -> {
                            if(!valid) sink.error(new NotEnoughBalanceException());
                        })
                        .then(proceedWithValidatedRequest(request, ucc));
            }
            default -> { return Mono.error(InvalidTransactionTypeException::new); }
        }
    }

    private Mono<? extends RiskOrderResponse> proceedWithValidatedRequest(OrderRequest request, String ucc) {
        switch (request.orderType()){
            case ORDER_TYPE_MARKET -> {return placeMarketOrder(request, ucc);}
            case ORDER_TYPE_LIMIT -> {return placeLimitOrder(request, ucc);}
            case ORDER_TYPE_STOP -> {return placeStopLossOrder(request, ucc);}
            default -> {return Mono.error(InvalidOrderTypeException::new);}
        }
    }

    private Mono<? extends RiskOrderResponse> placeStopLossOrder(OrderRequest request, String ucc) {
        return Mono.error(NotImplementedException::new);
    }

    private Mono<? extends RiskOrderResponse> placeLimitOrder(OrderRequest request, String ucc) {
        OrderEvent event = mapper.requestToOrderEvent(request, ucc, OrderStatus.VERIFIED);
        kafkaService.persistVerifiedOrder(event);
        return sendVerifiedEventToExchange(event);
    }

    private Mono<? extends RiskOrderResponse> placeMarketOrder(OrderRequest request, String ucc) {
        return Mono.error(NotImplementedException::new);
    }


    private Mono<? extends RiskOrderResponse> sendVerifiedEventToExchange(OrderEvent verifiedEvent) {
        return this.webClient.post()
                .uri(uri -> uri.path("").build())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(verifiedEvent))
                .exchangeToMono(response -> handleExchangeResponse(verifiedEvent, response))
                .onErrorResume(Mono::error);
    }

    private Mono<RiskOrderResponse> handleExchangeResponse(OrderEvent verifiedEvent, ClientResponse response) {
        return Mono.just(mapper.getRiskOrderResponse(verifiedEvent, response.statusCode()));
    }




    /* Will verify if the client has enough holdings to place the order */
    @Transactional(readOnly = true)
    public Mono<Boolean> checkIfAskRequestValid(OrderRequest request, String ucc) {
        return clientSecurityRepo.findByUccAndSymbol(ucc, request.symbol())
                .switchIfEmpty(Mono.error(NotEnoughSecurityToPlaceOrder::new))
                .map(size -> size >= request.quantity());
    }

    @Transactional(readOnly = true)
    public Mono<Boolean> checkIfBidRequestValid(OrderRequest request, String ucc) {
        return metaRepo.findClientBalanceByUCC(UUID.fromString(ucc))
                .switchIfEmpty(Mono.error(NotEnoughBalanceException::new))
                .map(balance -> balance >= (request.price() * request.quantity()));
    }


//      SECTION – 6: SMART ORDER ROUTING

//      6.1 Introduction of Smart Order Routing25
//      1. SEBI has received proposal from the stock exchanges and market participants for
//      introducing Smart Order Routing which allows the brokers trading engines to
//      systematically choose the execution destination based on factors viz. marketPrice, costs,
//      speed, likelihood of execution and settlemen
//    public Flux<SymbolUpdate> findLiquidity(OrderRequest request){
//
//        return Flux.error(NotImplementedException::new);
//    }

}
