package org.exchange.user.Config.Cors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.server.ServerWebExchange;

@Component
public class CorsConfigurationSourceImpl implements CorsConfigurationSource {


    @Value("${allowed.origin}")
    private String allowedOrigin;



    @Override
    public CorsConfiguration getCorsConfiguration(ServerWebExchange exchange) {

        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin(allowedOrigin);
        corsConfiguration.addAllowedMethod("GET");
        corsConfiguration.addAllowedMethod("POST");
        corsConfiguration.addAllowedMethod("PUT");
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setMaxAge(3600L);
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addExposedHeader("*");

        return corsConfiguration;
    }

}
