package com.exchange.riskmanagement.Repository;

import com.exchange.riskmanagement.Model.Entity.ClientMeta;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ClientMetaRepo extends R2dbcRepository<ClientMeta, Long> {

    @Query("SELECT cm.balance from client_meta as cm where cm.ucc = :ucc")
    Mono<Double> findClientBalanceByUCC(UUID ucc);
}
