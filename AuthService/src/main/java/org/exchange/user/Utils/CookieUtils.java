package org.exchange.user.Utils;

import org.exchange.library.Dto.Authentication.JwtResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.time.Duration;

@Component
public class CookieUtils {

    public void bakeCookies(ServerWebExchange webExchange, JwtResponse jwtResponse) {
        ResponseCookie accessCookie = ResponseCookie.from("ACCESS_TOKEN",
                        jwtResponse.accessToken())
                .sameSite("Lax")
                .httpOnly(true)
                .maxAge(Duration.ofDays(30))
                .path("/")
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("REFRESH_TOKEN",
                        jwtResponse.refreshToken())
                .sameSite("Lax")
                .httpOnly(true)
                .maxAge(Duration.ofDays(30))
                .path("/api/bainsight/auth/refresh")
                .build();
        
        webExchange.getResponse().addCookie(accessCookie);
        webExchange.getResponse().addCookie(refreshCookie);
    }

}
