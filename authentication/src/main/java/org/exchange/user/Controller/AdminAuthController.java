package org.exchange.user.Controller;


import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.exchange.library.Dto.Authentication.AdminAuthResponse;
import org.exchange.library.Dto.Authentication.AuthRequest;
import org.exchange.library.Mapper.ValidationErrorMapper;
import org.exchange.library.Utils.WebTrimmer;
import org.exchange.user.Service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/bainsight/auth/admin/")
@RequiredArgsConstructor
@Slf4j
public class AdminAuthController {

    private final AuthService authService;
    private final io.github.resilience4j.circuitbreaker.CircuitBreaker circuitBreaker;

    @InitBinder
    public void removeWhiteSpaces(WebDataBinder binder) {
        WebTrimmer.setCustomEditorForWebBinder(binder);
    }

    @PostMapping("/login")
    @CircuitBreaker(name = "auth-service")
    @Retry(name = "auth-service")
    public Mono<ResponseEntity<AdminAuthResponse>> loginAdmin(@Validated @RequestBody AuthRequest request, ServerWebExchange webExchange) {
        log.info("Admin login triggered!");


        return authService.loginAdmin(request, webExchange)
                .map(authResponse -> ResponseEntity.accepted().body(authResponse))

                .onErrorResume(ex -> {
                    if (ex instanceof WebExchangeBindException webBindException) {
                        return Mono.error(ValidationErrorMapper.fetchFirstError(webBindException));
                    }
                    return Mono.error(ex);
                }).transform(CircuitBreakerOperator.of(circuitBreaker));
    }


}
