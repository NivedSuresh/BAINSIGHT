package org.exchange.user.Controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.exchange.library.Dto.Authentication.TokenMeta;
import org.exchange.library.Utils.WebTrimmer;
import org.exchange.user.Service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/bainsight/auth")
public class TokenController {

    private final AuthService authService;

    @InitBinder
    public void removeWhiteSpaces(WebDataBinder binder) {
        WebTrimmer.setCustomEditorForWebBinder(binder);
    }

    @GetMapping("/refresh")
    public Mono<ResponseEntity<Void>> renewJwt(ServerHttpRequest request, ServerWebExchange exchange) {
        log.info("Inside renew method controller");
        return authService.renewJwt(request).map(jwt -> {
            ResponseCookie accessCookie = ResponseCookie.from("ACCESS_TOKEN", jwt)
                    .httpOnly(true)
                    .maxAge(Duration.ofDays(30))
                    .path("/")
                    .build();
            exchange.getResponse().addCookie(accessCookie);
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        });
    }

    @GetMapping("/validate")
    public ResponseEntity<Mono<TokenMeta>> validateToken(ServerWebExchange webExchange){
        System.out.println("Inside /validate");
        return ResponseEntity.ok(authService.validateToken(webExchange));
    }

}
