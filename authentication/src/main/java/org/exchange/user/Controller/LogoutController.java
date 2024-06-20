package org.exchange.user.Controller;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.exchange.library.Dto.Authentication.JwtResponse;
import org.exchange.user.Utils.CookieUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

@RequestMapping("/api/bainsight/auth/logout")
@RestController
@RequiredArgsConstructor
@CircuitBreaker(name = "auth-service")
@Retry(name = "auth-service")
public class LogoutController {

    private final CookieUtils cookieUtils;
    private final io.github.resilience4j.circuitbreaker.CircuitBreaker circuitBreaker;


    @GetMapping
    public ResponseEntity<Void> logoutUser(ServerWebExchange webExchange){
        cookieUtils.bakeCookies(webExchange, new JwtResponse("INVALID", "INVALID", ""));
        return ResponseEntity.ok().build();
    }


}
