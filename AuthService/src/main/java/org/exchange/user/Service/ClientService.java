package org.exchange.user.Service;

import org.exchange.library.Dto.Authentication.ClientAuthResponse;
import org.exchange.library.Dto.Authentication.ClientSignupRequest;
import org.exchange.user.Model.Client;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public interface ClientService {
    Mono<Client> findByPhone(String brokerId);

    Mono<ClientAuthResponse> save(ClientSignupRequest broker, ServerWebExchange webExchange);

}
