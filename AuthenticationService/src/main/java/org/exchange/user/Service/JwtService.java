package org.exchange.user.Service;

import org.exchange.library.Dto.Authentication.JwtResponse;
import reactor.core.publisher.Mono;

import java.time.Instant;

public interface JwtService {
    Mono<String> generateJwt(String identifier, String authority, Instant expiry);

    Mono<JwtResponse> getAuthResponse(String ucc, String authority, Instant forAccess, Instant forRefresh);
}
