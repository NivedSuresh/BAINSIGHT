package com.exchange.riskmanagement.Repository;

import com.exchange.riskmanagement.Model.Entity.SymbolMeta;
import org.exchange.library.Dto.Symbol.SymbolResponse;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface Symbol extends R2dbcRepository<SymbolMeta, Long> {

    @Query("SELECT t.symbol, t.exchange from symbol_meta as t")
    Flux<SymbolResponse> findAllFromDB();
    @Query("SELECT t.exchange FROM symbol_meta as t where t.symbol = :symbol")
    Flux<String> findAllExchangesBySymbol(String symbol);
}
