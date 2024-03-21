package org.bainsight.liquidity.Config.Sinks;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Sinks;

@Configuration
public class SinksConfig {

    @Bean
    public Sinks.Many<byte[]> messageSink(){
        return Sinks.many().unicast().onBackpressureBuffer();
    }

}
