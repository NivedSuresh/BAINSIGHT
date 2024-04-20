package com.bainsight.risk.Message.Kafka;


import com.bainsight.risk.Message.gRPC_Client.RiskManagementService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.exchange.library.KafkaEvent.DailyOrderMetaEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DailyOrderMetaEventListener {


    private final RiskManagementService riskManagementService;
    private final ObjectMapper mapper;


    @KafkaListener(groupId = "decreasable", topics = "daily-order-meta-decrease-event")
    public void listenToDailyOrderMetaEvents(String dailyOrderMetaString){
        try {
            DailyOrderMetaEvent dailyOrderMetaEvent = this.mapper.readValue(dailyOrderMetaString, DailyOrderMetaEvent.class);
            this.riskManagementService.decreaseDailyOrderMeta(dailyOrderMetaEvent);
        } catch (JsonProcessingException e) {
            log.error("Failed to Read Value from string: {}", dailyOrderMetaString);
        }
    }


}
