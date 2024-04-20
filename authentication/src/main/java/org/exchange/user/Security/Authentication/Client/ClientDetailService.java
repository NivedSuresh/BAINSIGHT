package org.exchange.user.Security.Authentication.Client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.exchange.library.Exception.Authentication.BadBindException;
import org.exchange.library.Exception.IO.ServiceUnavailableException;
import org.exchange.user.Service.ClientService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientDetailService implements ReactiveUserDetailsService {
    private final ClientService clientService;

    @Override
    public Mono<UserDetails> findByUsername(String phoneNumber) {

        log.info("Inside find by username with brokerId {}", phoneNumber);

        return clientService.findByPhone(phoneNumber)
                .handle((client, sink) -> {
                    log.info("Client : {}", client);
                    if (client == null) {
                        log.error("Failed to fetch Client from Database with the provided tag!");
                        sink.error(new BadBindException());
                        return;
                    }
                    sink.next(new ClientDetails(
                            client.getUcc().toString(),
                            client.getPassword(),
                            client.getRevoked(),
                            List.of("BROKER"),
                            client.getUsername(),
                            client.getPhoneNumber(),
                            client.getEmail()
                    ));
                }).map(o -> (UserDetails) o)
                .doOnError(throwable -> {
                    log.error("Exception caught: {}", throwable.getMessage());
                    if (throwable instanceof BadBindException)
                        throw (BadBindException) throwable;

                    throw new ServiceUnavailableException();
                });
    }
}
