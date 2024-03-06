package com.exchange.gateway.Config.Filters;

import lombok.extern.slf4j.Slf4j;
import org.exchange.library.Advice.Error;
import org.exchange.library.Exception.Authorization.InvalidJwtException;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


/* This filter will extract the 'principal.name' property and will append it with
    the header, thus making the field available in every other service */
@Slf4j
@Service
public class HeaderManipulationFilter extends AbstractGatewayFilterFactory<HeaderManipulationFilter.Config> {


    public HeaderManipulationFilter(ReactiveJwtDecoder jwtDecoder) {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            return ReactiveSecurityContextHolder.getContext()
                    /*
                       The ReactiveSecurityContextHolder is associated with the Reactor Context.
                       This context propagates throughout the reactive stream making it
                       accessible here as well.
                    */
                    .map(SecurityContext::getAuthentication)
                    .switchIfEmpty(Mono.error(new InvalidJwtException(Error.INVALID_JWT)))
                    .flatMap(authentication -> {
                        exchange.getRequest().mutate().header("x-auth-user-id", authentication.getName());
                        return chain.filter(exchange);
                    });
        };
    }

    public static class Config {
    }

}
