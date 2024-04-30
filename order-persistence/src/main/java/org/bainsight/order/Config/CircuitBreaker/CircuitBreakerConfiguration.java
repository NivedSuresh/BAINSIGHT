package org.bainsight.order.Config.CircuitBreaker;


import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;

import javax.naming.ServiceUnavailableException;
import java.time.Duration;

@Configuration
public class CircuitBreakerConfiguration {

    @Bean
    public CircuitBreaker circuitBreaker(){
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .slidingWindowSize(5)
                .minimumNumberOfCalls(5)
                .enableAutomaticTransitionFromOpenToHalfOpen()
                .permittedNumberOfCallsInHalfOpenState(3)
                .waitDurationInOpenState(Duration.ofSeconds(5))
                .failureRateThreshold(50)
                .recordException(throwable ->
                        throwable instanceof ServiceUnavailableException ||
                        throwable instanceof DataAccessException
                )
                .build();

        return CircuitBreakerRegistry.of(config).circuitBreaker("order-persistence");
    }


}
