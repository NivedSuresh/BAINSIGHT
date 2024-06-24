package org.bainsight.updates.Message.Aeron;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.aeron.Aeron;
import io.aeron.Subscription;
import io.aeron.driver.MediaDriver;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.agrona.CloseHelper;
import org.agrona.concurrent.SigInt;
import org.bainsight.updates.Domain.WebSocketService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@EnableScheduling
@Profile("UDP")
public class CandleStickListener {

    private final ObjectMapper mapper;
    private final WebSocketService webSocketService;

    @Value("${user.service.channel}")
    String USER_SERVICE_CHANNEL;
    private final static Integer USER_SERVICE_STREAM_ID;
    private static final ExecutorService LISTENER_THREAD;
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final MediaDriver mediaDriver;
    private final Aeron aeron;

    static {
        USER_SERVICE_STREAM_ID = Integer.getInteger("aeron.user.service.multicast.steam.id",1001);
        LISTENER_THREAD = Executors.newSingleThreadExecutor();
    }



    public void handle()
    {
        // Register a SIGINT handler for graceful shutdown.
        SigInt.register(() -> running.set(false));

        CandleStickHandler candleStickHandler = new CandleStickHandler(mapper, webSocketService);


        LISTENER_THREAD.execute(() -> {
            System.out.println("user service channel: " + USER_SERVICE_CHANNEL);
            try (Subscription subscription = aeron.addSubscription(USER_SERVICE_CHANNEL,
                                                                USER_SERVICE_STREAM_ID)) {
                while (running.get()) {
                    subscription.poll(candleStickHandler, 10);
                }
            }
            catch (Exception e) {
                /* TODO: IMPLEMENT JOURNALING */
            }

            CloseHelper.close(mediaDriver);
        });
    }



    @PostConstruct
    @Scheduled(cron = "0 0 8 * * *") // Run at 8:00 AM every day
    public void setFlagToTrue() {
        if (isTimeWithinRange(LocalTime.of(0, 0), LocalTime.of(23, 59))) {
            this.running.set(true);
            this.handle();
        }
    }


    @Scheduled(cron = "0 0 16 * * *") // Run at 4:00 PM every day
    public void setFlagToFalse() {
        if (isTimeWithinRange(LocalTime.of(8, 0), LocalTime.of(16, 0))) {
            this.running.set(false);
        }
    }

    private boolean isTimeWithinRange(LocalTime startTime, LocalTime endTime) {
        LocalTime currentTime = LocalTime.now();
        return !currentTime.isBefore(startTime) && !currentTime.isAfter(endTime);
    }

}
