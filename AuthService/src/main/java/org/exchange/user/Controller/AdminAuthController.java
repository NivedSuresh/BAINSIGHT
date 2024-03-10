package org.exchange.user.Controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.exchange.library.Dto.Authentication.AdminAuthResponse;
import org.exchange.library.Dto.Authentication.AuthRequest;
import org.exchange.library.Dto.Authentication.JwtResponse;
import org.exchange.library.Mapper.ValidationErrorMapper;
import org.exchange.library.Utils.WebTrimmer;
import org.exchange.user.Service.AuthService;
import org.springframework.boot.web.server.Cookie;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/bainsight/auth/admin/")
@RequiredArgsConstructor
@Slf4j
public class AdminAuthController {

    private final AuthService authService;

    @InitBinder
    public void removeWhiteSpaces(WebDataBinder binder) {
        WebTrimmer.setCustomEditorForWebBinder(binder);
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<AdminAuthResponse>> loginAdmin(@Validated @RequestBody AuthRequest request, ServerWebExchange webExchange) {
        log.info("Admin login triggered!");


        return authService.loginAdmin(request, webExchange)
                .map(authResponse -> ResponseEntity.accepted().body(authResponse))

                .onErrorResume(ex -> {
                    if (ex instanceof WebExchangeBindException webBindException) {
                        return Mono.error(ValidationErrorMapper.fetchFirstError(webBindException));
                    }
                    return Mono.error(ex);
                });
    }


}
