package org.exchange.user.Service;

import org.exchange.library.Dto.Authentication.ClientSignupRequest;
import org.exchange.library.Dto.Authentication.ClientAuthResponse;
import org.exchange.user.Model.Client;
import reactor.core.publisher.Mono;

public interface ClientService {
    Mono<Client> findByPhone(String brokerId);

    Mono<ClientAuthResponse> save(ClientSignupRequest broker);

}
