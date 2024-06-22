package org.bainsight.data.Simulation;


import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
class MarketDataJob {

    private final MarketDataEngine engine;
    private final ExecutorService greenExecutor;

    @SneakyThrows
    @Scheduled(cron = "0/3 * * * * *")
    public void multicastMessages(){
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
        LocalDateTime localDateTime = now.toLocalDateTime();
        LocalTime currentTime = now.toLocalTime();

        // Check if it's Sunday or outside the 9 AM to 5 PM range
        if (localDateTime.getDayOfWeek().equals(DayOfWeek.SUNDAY) || currentTime.isBefore(LocalTime.of(9, 0)) || currentTime.isAfter(LocalTime.of(17, 0))) {
            return;
        }
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
