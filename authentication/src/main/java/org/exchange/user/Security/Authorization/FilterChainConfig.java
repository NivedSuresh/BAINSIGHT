package org.exchange.user.Security.Authorization;


import org.exchange.user.Security.Authentication.Admin.AdminAuthenticationManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

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
        security.cors(ServerHttpSecurity.CorsSpec::disable);

        security.authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec.anyExchange().permitAll());

        security.authenticationManager(authenticationManager);

        return security.build();
    }


}
