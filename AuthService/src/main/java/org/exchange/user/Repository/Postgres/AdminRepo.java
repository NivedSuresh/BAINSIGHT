package org.exchange.user.Repository.Postgres;


import org.exchange.user.Model.Admin;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface AdminRepo extends R2dbcRepository<Admin, Long> {
    Mono<Admin> findByEmail(String email);


    @Query("SELECT a.is_banned from admin as a where a.email = :email")
    Mono<Boolean> getPrincipalValidation(String email);
}
