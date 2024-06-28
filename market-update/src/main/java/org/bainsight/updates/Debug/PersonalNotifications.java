package org.bainsight.updates.Debug;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bainsight.updates.Domain.WebSocketService;
import org.bainsight.updates.Message.Kafka.KafkaPrivateMessageListener;
import org.exchange.library.Dto.Notification.Notification;
import org.exchange.library.Dto.Notification.NotificationStatus;
import org.exchange.library.Dto.Notification.Ucc;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@EnableScheduling
@Service
@RequiredArgsConstructor
@Profile("debug")
@Slf4j
public class PersonalNotifications
{

    private final KafkaPrivateMessageListener listener;
    private final ObjectMapper mapper;

    @Scheduled(fixedRate = 10, timeUnit = TimeUnit.SECONDS)
    public void sendPrivate()
    {
        try {
            this.listener.listenToNotifications(
                    mapper.writeValueAsString(
                    new Notification<>("bdf7d275-c0ab-454d-87d6-a0d38b0e0794",
                            "bdf7d275-c0ab-454d-87d6-a0d38b0e0794",
                            new Ucc("bdf7d275-c0ab-454d-87d6-a0d38b0e0794"),
                            NotificationStatus.SUCCESS))
            );
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }

}
