package com.bainsight.risk.Message.Kafka;


import com.bainsight.risk.Message.gRPC_Client.RiskManagementService;
import com.bainsight.risk.Model.Entity.DailyOrderMeta;
import com.bainsight.risk.Data.DailyOrderMetaRepo;
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
    private final DailyOrderMetaRepo dailyOrderMetaRepo;



    @KafkaListener(groupId = "risk-management" , topics = "risk-processing-rollback")
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

    @KafkaListener(topics = "open-order-count-decrement", groupId = "risk-management")
    public void decreaseOpenQuantity(String ucc){
        try{
            ucc = ucc.substring(1, ucc.length() - 1);
            DailyOrderMeta dailyOrderMeta = this.dailyOrderMetaRepo.findById(ucc).orElse(new DailyOrderMeta(ucc, 1, 0.0));
            dailyOrderMeta.setOpenOrderCount(dailyOrderMeta.getOpenOrderCount() - 1);
            this.dailyOrderMetaRepo.save(dailyOrderMeta);
        }
        catch (RuntimeException ex){
            /* TODO: IMPLEMENT LOGGING IF NEEDED */
        }
    }


}
