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
    private final AtomicInteger count = new AtomicInteger(0);

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.SECONDS)
    public void multicastMessagesPrimary(){
        primary.execute(this::iterateAndSend);
    }

    @Scheduled(initialDelay = 200, fixedRate = 1000)
    public void multicastMessagesBackup(){
        backup.execute(this::iterateAndSend);
    }

    private void iterateAndSend() {
        for(int i=0 ; i<25 ; i++){
            engine.sendUpdates(count.incrementAndGet() % 2 == 1);
        }
    }


}
