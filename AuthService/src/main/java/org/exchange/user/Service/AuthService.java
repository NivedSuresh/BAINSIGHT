package org.exchange.user.Service;

import org.exchange.library.Dto.Authentication.AdminAuthResponse;
import org.exchange.library.Dto.Authentication.AuthRequest;
import org.exchange.library.Dto.Authentication.ClientAuthResponse;
import org.exchange.library.Dto.Authentication.JwtResponse;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;

public interface AuthService {
    Mono<String> renewJwt(ServerHttpRequest request);

    Mono<ClientAuthResponse> loginClient(AuthRequest request, ServerWebExchange webExchange);

    Mono<AdminAuthResponse> loginAdmin(AuthRequest request, ServerWebExchange webExchange);

    Mono<JwtResponse> getJwtResponse(String ucc, String authority, Instant forAccess, Instant forRefresh);

}
