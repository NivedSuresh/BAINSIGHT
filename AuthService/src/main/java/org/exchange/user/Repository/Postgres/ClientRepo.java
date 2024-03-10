package org.exchange.user.Repository.Postgres;

import org.exchange.user.Model.Client;
import org.exchange.user.Model.PrincipalRevoked;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;


public interface ClientRepo extends R2dbcRepository<Client, UUID> {


    @Query("SELECT c.revoked FROM  client AS c WHERE c.ucc = :ucc")
    Mono<PrincipalRevoked> getPrincipalValidationForClient(UUID ucc);

    Mono<Client> findByPhoneNumber(String phone);

    @Query("SELECT EXISTS(SELECT 1 FROM client c WHERE c.phone_number = :phoneNumber)")
    Mono<Boolean> existsByPhoneNumber(String phoneNumber);
}
