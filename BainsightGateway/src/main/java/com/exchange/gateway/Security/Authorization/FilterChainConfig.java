package com.exchange.gateway.Security.Authorization;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Collection;

@Configuration
@RequiredArgsConstructor
@EnableWebFluxSecurity
public class FilterChainConfig {


//    private final CorsConfigurationSource configurationSource;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity security) {

        security.csrf(ServerHttpSecurity.CsrfSpec::disable);

//        security.cors(cors -> cors.configurationSource(configurationSource));

        security.authorizeExchange(exchange -> exchange.anyExchange().permitAll());
//
//        security.authorizeExchange(auth -> auth
//                .pathMatchers("/eureka/**").permitAll()
//
//                /* This endpoint receives phone number and credentials.
//                   If authenticated, then Access & Refresh tokens are returned. */
//                .pathMatchers("/api/bainsight/token/**").permitAll()
//
//
//                .pathMatchers("/api/bainsight/auth/**").permitAll()
//
//
//                /* Both Admin and User can submit refresh token as header and can
//                   get a new access token. */
//                .pathMatchers("/api/bainsight/token").hasAnyAuthority("CLIENT_REFRESH_TOKEN", "ADMIN_REFRESH_TOKEN")
//
//                .pathMatchers("/api/bainsight/client/signup").permitAll()
//
//                /* This endpoint receives admin credentials.
//                   If authenticated, then Access & Refresh tokens are returned. */
//                .pathMatchers("/api/bainsight/admin/login").permitAll()
//
//                /* Every request to this endpoint will be Manipulated by the HeaderManipulationFilter. */
//                .pathMatchers("/api/bainsight/risk/order/**").hasAnyAuthority("CLIENT", "ADMIN")
//
//                .pathMatchers("/api/bainsight/risk/admin/**").hasAuthority("ADMIN")
//
//
//                .pathMatchers("/healthcheck/**", "/api/bainsight/admin/**").hasAuthority("ADMIN")
//
//                .anyExchange().hasAuthority("CLIENT"));

        return security.build();
    }

    private Mono<? extends AbstractAuthenticationToken> createAuthenticationToken(Jwt source) {
        return Mono.just(new JwtAuthenticationToken(source, extractAuthorities(source)));
    }

    private Collection<? extends GrantedAuthority> extractAuthorities(Jwt source) {
        return AuthorityUtils.createAuthorityList(source.getClaimAsString("authority"));
    }

}
