package org.bainsight.updates.Message.Kafka;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.exchange.library.Dto.Notification.Notification;
import org.bainsight.updates.Domain.WebSocketService;
import org.exchange.library.Dto.Notification.Ucc;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaPrivateMessageListener {

    private final ObjectMapper objectMapper;
    private final WebSocketService webSocketService;

    @KafkaListener(topics = "private-notification", groupId = "user-layer")
    public void listenToNotifications(String json) {
        try{
            TypeReference<Notification<Ucc>> typeRef = new TypeReference<>() {};
            Notification<? extends Ucc> notification = this.objectMapper.readValue(json, typeRef);
            log.info("New Message: {}", notification.message());
            this.webSocketService.pushNotification(notification.data().ucc(), notification);
        }
        catch (JsonProcessingException ex){
            log.info("Failed to process message: {}", json);
        }
    }


}
