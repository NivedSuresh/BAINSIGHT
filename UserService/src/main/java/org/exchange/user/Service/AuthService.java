package org.exchange.user.Service;

import org.exchange.library.Dto.Authentication.AdminAuthResponse;
import org.exchange.library.Dto.Authentication.AuthRequest;
import org.exchange.library.Dto.Authentication.ClientAuthResponse;
import org.exchange.library.Dto.Authentication.JwtResponse;
import reactor.core.publisher.Mono;

import java.time.Instant;

public interface AuthService {
    Mono<String> renewJwt(String refreshToken);

    Mono<ClientAuthResponse> loginClient(AuthRequest request);

    Mono<AdminAuthResponse> loginAdmin(AuthRequest request);

    Mono<JwtResponse> getJwtResponse(String ucc, String authority, Instant forAccess, Instant forRefresh);
}
