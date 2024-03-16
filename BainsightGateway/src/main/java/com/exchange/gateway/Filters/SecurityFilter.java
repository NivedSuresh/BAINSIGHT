package com.exchange.gateway.Filters;

import com.exchange.gateway.Filters.Helper.EndpointsUtil;
import lombok.extern.slf4j.Slf4j;
import org.exchange.library.Exception.Authorization.InvalidJwtException;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;


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

            System.out.println("Inside filter");

            for(String key : exchange.getRequest().getCookies().keySet()){
                System.out.println("Key : " + key);
            }


            ServerHttpRequest request = exchange.getRequest();
            String path = request.getPath().toString();

            if(endpointsUtil.checkIfOpenEndpoint(path)){
                System.out.println("open endpoint");
                return chain.filter(exchange);
            }

            HttpCookie cookie = request.getCookies().getFirst("ACCESS_TOKEN");

            if(cookie == null) return Mono.error(InvalidJwtException::new);

            return jwtDecoder.decode(cookie.getValue())
                    .flatMap(token -> {

                        System.out.println(token.getSubject() + " " + token.getClaim("authority"));

                        if(endpointsUtil.isAllowed(path, token.getClaim("authority"))){
                            return updateSecurityContextHolder(token).flatMap(securityContext -> {
                                return chain.filter(exchange);
                            });
                        }
                        System.out.println("User not allowed for path ".concat(path));
                        return Mono.error(InvalidJwtException::new);
                    })

                    .onErrorResume(throwable -> {
                        if(throwable instanceof NullPointerException){
                            return chain.filter(exchange);
                        }
                        return Mono.error(InvalidJwtException::new);
                    });

        };
    }

    private Mono<SecurityContext> updateSecurityContextHolder(Jwt jwt) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(jwt, null);
        return ReactiveSecurityContextHolder.getContext()
                .doOnNext(securityContext -> {
                    securityContext.setAuthentication(authentication);
                });
    }
}
