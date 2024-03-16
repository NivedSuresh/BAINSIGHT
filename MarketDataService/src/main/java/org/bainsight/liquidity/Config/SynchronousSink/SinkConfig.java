package org.bainsight.liquidity.Config.SynchronousSink;

import org.exchange.library.Dto.MarketRelated.Tick;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Sinks;

@Configuration
public class SinkConfig {

    @Bean
    public Sinks.Many<String> messageSink(){
        return Sinks.many().unicast().onBackpressureBuffer();
    }
    @Bean
    public Sinks.Many<Tick> tickSink(){
        return Sinks.many().multicast().directBestEffort();
    }

}
