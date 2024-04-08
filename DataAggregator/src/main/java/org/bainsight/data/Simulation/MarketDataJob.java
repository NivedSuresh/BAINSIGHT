package org.bainsight.data.Simulation;


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
    private final ExecutorService greenExecutor;

    @SneakyThrows
    @Scheduled(fixedRate = 3, timeUnit = TimeUnit.SECONDS)
    public void multicastMessages(){
        greenExecutor.execute(() -> iterateAndSend(true));
        Thread.sleep(200);
        greenExecutor.execute(() -> iterateAndSend(false));
    }


    private void iterateAndSend(boolean isPrimary) {
        for(int i=0 ; i<1 ; i++){
            try {
                engine.sendUpdates(isPrimary);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }


}
