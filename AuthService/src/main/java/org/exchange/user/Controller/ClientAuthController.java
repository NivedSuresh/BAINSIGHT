package org.exchange.user.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.exchange.library.Dto.Authentication.AuthRequest;
import org.exchange.library.Dto.Authentication.ClientSignupRequest;
import org.exchange.library.Dto.Authentication.ClientAuthResponse;
import org.exchange.library.Mapper.ValidationErrorMapper;
import org.exchange.user.Service.AuthService;
import org.exchange.user.Service.ClientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/bainsight/auth/client")
@RequiredArgsConstructor
@Slf4j
public class ClientAuthController {

    private final ClientService clientService;
    private final AuthService authService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/signup")
    public Mono<ClientAuthResponse> saveClient(@Validated @RequestBody ClientSignupRequest request) {
        System.out.println(request);
        return clientService.save(request);
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<ClientAuthResponse>> loginClient(@Validated @RequestBody AuthRequest request) {
        log.info("Inside method, Request : {}", request);

        return authService.loginClient(request)
                .map(s -> ResponseEntity.status(HttpStatus.ACCEPTED).body(s))
                .onErrorResume(throwable -> {
                    if (throwable instanceof WebExchangeBindException webBindException) {
                        return Mono.error(ValidationErrorMapper.fetchFirstError(webBindException));
                    }
                    return Mono.error(throwable);
                });

    }

}
