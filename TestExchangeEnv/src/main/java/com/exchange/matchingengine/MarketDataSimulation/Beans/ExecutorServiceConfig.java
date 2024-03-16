package com.exchange.matchingengine.MarketDataSimulation.Beans;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorServiceConfig {
    @Bean
    public ExecutorService primary(){
        return Executors.newSingleThreadExecutor();
    }

    @Bean
    public ExecutorService backup(){
        return Executors.newSingleThreadExecutor();
    }
}
