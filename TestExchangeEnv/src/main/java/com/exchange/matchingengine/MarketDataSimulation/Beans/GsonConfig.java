package com.exchange.matchingengine.MarketDataSimulation.Beans;

import com.exchange.matchingengine.MarketDataSimulation.Adapters.InstantAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;

@Configuration
public class GsonConfig {

    @Bean
    public Gson gson(){
        return new GsonBuilder()
                .registerTypeAdapter(Instant.class, new InstantAdapter())
                .create();
    }

}
