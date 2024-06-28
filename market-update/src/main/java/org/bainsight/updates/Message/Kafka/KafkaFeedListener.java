package org.bainsight.updates.Message.Kafka;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.bainsight.updates.Domain.WebSocketService;
import org.exchange.library.Dto.MarketRelated.CandleStick;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Profile("TCP")
public class KafkaFeedListener {

    private final ObjectMapper mapper;
    private final WebSocketService webSocketService;

    @KafkaListener(topics = "live_update_tcp", groupId = "#{T(java.util.UUID).randomUUID().toString()}")
    public void listenToUpdates(final String jsonStick) throws JsonProcessingException {
        CandleStick stick = this.mapper.readValue(jsonStick, CandleStick.class);
        this.webSocketService.pushRequested(stick);
    }

}
