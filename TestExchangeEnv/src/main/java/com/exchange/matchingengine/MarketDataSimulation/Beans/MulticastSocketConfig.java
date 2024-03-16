package com.exchange.matchingengine.MarketDataSimulation.Beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.MulticastSocket;

@Configuration
public class MulticastSocketConfig {

    @Bean
    public MulticastSocket multicastSocket() throws IOException {
        return new MulticastSocket();
    }

}
