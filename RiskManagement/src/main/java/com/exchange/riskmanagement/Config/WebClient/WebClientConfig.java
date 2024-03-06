package com.exchange.riskmanagement.Config.WebClient;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    @LoadBalanced
    public WebClient.Builder builder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder
                .baseUrl("http://localhost:8080/api")
//                .filter((request, next) -> {
//                    builder.defaultHeader("Authorization", request.headers().getFirst("Authorization"));
//                    return next.exchange(request);
//                })
                .build();
    }

}
