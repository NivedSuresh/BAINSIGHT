package org.exchange.user.Security.Authentication.Client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.exchange.library.Exception.Authentication.InvalidCredentialsException;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;

import java.util.List;


@RequiredArgsConstructor
@Slf4j
public class ClientAuthenticationManager implements ReactiveAuthenticationManager {

    private final ClientDetailService clientDetailService;
    private final PasswordEncoder encoder;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {

        log.info("Authentication : {}", authentication);

        return clientDetailService.findByUsername(authentication.getName())
                .switchIfEmpty(Mono.error(new InvalidCredentialsException("Failed to find Client with the provided details.")))
                .handle((userDetails, sink) -> {
                    log.info("Principal fetched : {}", userDetails);
                    if (userDetails == null) {
                        log.error("Fetched principal is null!");
                        sink.error(new InvalidCredentialsException("Failed to find Client with the provided details."));
                        return;
                    }
                    if (!encoder.matches(authentication.getCredentials().toString(), userDetails.getPassword())) {
                        log.error("Password doesn't match!");
                        sink.error(new InvalidCredentialsException());
                        return;
                    }
                    //Will verify if account is banned/revoked
                    try {
                        new AccountStatusUserDetailsChecker().check(userDetails);
                    } catch (Exception e) {
                        sink.error(new InvalidCredentialsException("Account has been Invoked!"));
                        return;
                    }
                    log.info("Authentication object has been returned");
                    sink.next(new UsernamePasswordAuthenticationToken(
                            userDetails, authentication.getCredentials(), List.of(new SimpleGrantedAuthority("CLIENT")))
                    );
                });
    }
}
