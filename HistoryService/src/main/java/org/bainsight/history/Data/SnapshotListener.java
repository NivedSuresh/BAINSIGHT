package org.bainsight.history.Data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bainsight.history.Models.Dto.CandleStick;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SnapshotListener {

    private static final String GROUP = "snapshot";
    private final ObjectMapper mapper;
    private final HistoryServiceImpl historyService;

    @KafkaListener(groupId = GROUP, topics = "candle_sticks")
    public void listen(String candleStick){
        try
        {
            CandleStick stick = mapper.readValue(candleStick, CandleStick.class);
            historyService.saveCandleStick(stick);
        }
        catch (JsonProcessingException e)
        {
            /* TODO: Implement logging! */
            log.error(e.getMessage());
        }
    }

}
