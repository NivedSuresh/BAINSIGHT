package com.exchange.riskmanagement.Repository;

import com.exchange.riskmanagement.Model.Entity.ClientSymbol;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface ClientSecurityRepo extends R2dbcRepository<ClientSymbol, Long> {

    @Query("select ct.size from client_symbol as ct where ct.ucc = :ucc AND ct.symbol = :symbol")
    Mono<Long> findByUccAndSymbol(String ucc, String symbol);
}
