package org.exchange.user.Utils;

import lombok.extern.slf4j.Slf4j;
import org.exchange.library.Dto.Authentication.JwtResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.time.Duration;

@Component
@Slf4j
public class CookieUtils {

    @Value("${bainsight.domain}")
    String domain;

    @Value("${bainsight.same-site}")
    String sameSite;


    public void bakeCookies(ServerWebExchange webExchange, JwtResponse jwtResponse) {

        log.info("Domain set for cookie to be returned: {}", domain);
        log.info("Same site config: {}", sameSite);

        ResponseCookie accessCookie = ResponseCookie.from("ACCESS_TOKEN",
                        jwtResponse.accessToken())
                .domain(domain)
                .sameSite("Lax")
                .secure(true)
                .httpOnly(true)
                .maxAge(Duration.ofDays(30))
                .path("/")
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("REFRESH_TOKEN",
                        jwtResponse.refreshToken())
                .domain(domain)
                .sameSite("Lax")
                .httpOnly(true)
                .secure(true)
                .maxAge(Duration.ofDays(30))
                .path("/api/bainsight/auth/refresh")
                .build();
        
        webExchange.getResponse().addCookie(accessCookie);
        webExchange.getResponse().addCookie(refreshCookie);
    }

}
