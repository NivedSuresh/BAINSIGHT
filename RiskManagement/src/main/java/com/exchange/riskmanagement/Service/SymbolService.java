package com.exchange.riskmanagement.Service;

import org.exchange.library.Dto.Symbol.SymbolRequest;
import org.exchange.library.Dto.Symbol.SymbolResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SymbolService {
    Flux<SymbolResponse> findAllSymbols();

    Mono<SymbolResponse> saveSymbol(SymbolRequest request);
}
