package org.exchange.library.Dto.Authentication;

import lombok.Builder;


@Builder
public record JwtResponse(
        String accessToken,
        String refreshToken,
        String message
) {
}
