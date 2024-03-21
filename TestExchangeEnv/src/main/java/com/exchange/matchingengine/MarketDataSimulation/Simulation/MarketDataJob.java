package com.exchange.matchingengine.MarketDataSimulation.Simulation;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
class MarketDataJob {

    private final MarketDataEngine engine;
    private final ExecutorService primary;
    private final ExecutorService backup;


    @SneakyThrows
    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.SECONDS)
    public void multicastMessages(){
        primary.execute(() -> iterateAndSend(true));
        Thread.sleep(200);
        backup.execute(() -> iterateAndSend(false));
    }


    private void iterateAndSend(boolean isPrimary) {
        for(int i=0 ; i<1 ; i++){
            try {
                engine.sendUpdates(isPrimary);
            } catch (JsonProcessingException e) {
                System.out.println(e.getMessage());
            }
        }
    }


}
