package org.exchange.user.Security.Authorization;


import org.exchange.user.Security.Authentication.Admin.AdminAuthenticationManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class FilterChainConfig {

    private final AdminAuthenticationManager authenticationManager;

    public FilterChainConfig(@Qualifier("ADMIN_AUTH_MANAGER") AdminAuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity security) {
        security.csrf(ServerHttpSecurity.CsrfSpec::disable);
//        security.authorizeExchange(exc -> exc.pathMatchers("/api/bainsight/token/renew/refresh_token").permitAll()
//                .pathMatchers("/api/bainsight/admin/auth/**").permitAll()
//                .pathMatchers("/api/bainsight/token/renew/jwt").authenticated()
//                .anyExchange().hasAuthority("ADMIN"));

        security.authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec.anyExchange().permitAll());

        security.authenticationManager(authenticationManager);

//        security.oauth2ResourceServer(oAuth -> {
//            oAuth.jwt(jwtSpec -> jwtSpec.jwtAuthenticationConverter(this::convertAuthentication));
//        });
        return security.build();
    }

    private Mono<? extends AbstractAuthenticationToken> convertAuthentication(Jwt source) {
        return Mono.just(new JwtAuthenticationToken(source,
                List.of(new SimpleGrantedAuthority(source.getClaim("authority")))));
    }


}
