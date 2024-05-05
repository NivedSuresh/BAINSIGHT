package com.exchange.gateway.Filters;

import com.exchange.gateway.Filters.Helper.EndpointsUtil;
import lombok.extern.slf4j.Slf4j;
import org.exchange.library.Exception.Authorization.InvalidJwtException;
import org.exchange.library.Exception.IO.ServiceUnavailableException;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;


@Component
@Slf4j
public class SecurityFilter extends AbstractGatewayFilterFactory<SecurityFilter.Config> {

    public static class Config {}

    private final ReactiveJwtDecoder jwtDecoder;
    private final EndpointsUtil endpointsUtil;

    public SecurityFilter(ReactiveJwtDecoder jwtDecoder, EndpointsUtil endpointsUtil) {
        super(Config.class);
        this.endpointsUtil = endpointsUtil;
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {

            ServerHttpRequest request = exchange.getRequest();
            String path = request.getPath().toString();

            if(endpointsUtil.checkIfOpenEndpoint(path))
            {
                return chain.filter(exchange);
            }

            HttpCookie cookie = request.getCookies().getFirst("ACCESS_TOKEN");

            if(cookie == null)
            {
                return Mono.error(InvalidJwtException::new);
            }

            return jwtDecoder.decode(cookie.getValue())
                    .flatMap(token -> {
                        if(endpointsUtil.isAllowed(path, token.getClaim("authority")))
                        {
                            exchange.getRequest().mutate().header("x-auth-user-id", token.getSubject());
                            return chain.filter(exchange);
                        }
                        log.error("User not allowed for path ".concat(path));
                        return Mono.error(InvalidJwtException::new);
                    })

                    .onErrorResume(throwable -> {
                        throwable.printStackTrace();
                        if(throwable instanceof InvalidJwtException) return Mono.error(throwable);
                        return Mono.error(ServiceUnavailableException::new);
                    });

        };
    }


}
