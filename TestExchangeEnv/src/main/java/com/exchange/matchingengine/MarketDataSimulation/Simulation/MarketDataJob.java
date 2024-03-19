package com.exchange.matchingengine.MarketDataSimulation.Simulation;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
class MarketDataJob {

    private final MarketDataEngine engine;
    private final ExecutorService primary;
    private final ExecutorService backup;
//
    @Scheduled(fixedRate = 3, timeUnit = TimeUnit.SECONDS)
    public void multicastMessagesPrimary(){
        primary.execute(() -> iterateAndSend(true));
    }

    @Scheduled(initialDelay = 200, fixedRate = 3000)
    public void multicastMessagesBackup(){
        backup.execute(() -> iterateAndSend(false));
    }

    private void iterateAndSend(boolean isPrimary) {
        for(int i=0 ; i<1 ; i++){
            try {
                engine.sendUpdates(isPrimary);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }
    }


}
