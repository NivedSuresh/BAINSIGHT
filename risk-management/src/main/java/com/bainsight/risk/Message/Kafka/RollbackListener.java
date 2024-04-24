package com.bainsight.risk.Message.Kafka;


import com.bainsight.risk.Message.gRPC_Client.RiskManagementService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.exchange.library.Enums.OrderType;
import org.exchange.library.Enums.TransactionType;
import org.exchange.library.KafkaEvent.RollbackEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RollbackListener {


    private final RiskManagementService riskManagementService;
    private final ObjectMapper mapper;



    @KafkaListener(groupId = "risk-processing-rollback", topics = "risk-rollback")
    public void rollbackRiskProcessing(String rollbackString){
        try {
            RollbackEvent rollbackEvent = this.mapper.readValue(rollbackString, RollbackEvent.class);
            this.riskManagementService.rollBackDailyOrderMeta(rollbackEvent);
            boolean isMarketBid = rollbackEvent.getOrderType() == OrderType.ORDER_TYPE_MARKET && rollbackEvent.getTransactionType() == TransactionType.BID;
            if(!isMarketBid) this.riskManagementService.portfolioValidationRollback(rollbackEvent);
        } catch (RuntimeException | JsonProcessingException e) {
            log.error("Failed to Read Value from string: {}", rollbackString);
        }
    }


}
