package org.bainsight.portfolio.Kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bainsight.portfolio.Data.PortfolioService;
import org.bainsight.portfolio.Data.WalletService;
import org.exchange.library.Enums.TransactionType;
import org.exchange.library.KafkaEvent.RollbackEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaProducerException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class RollbackListener {

    private final PortfolioService portfolioService;
    private final WalletService walletService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper mapper;


    /* TODO: ADD GROUP_ID AND TOPIC */
    @KafkaListener(groupId = "portfolio-validation-rollback", topics = "portfolio-validation-rollback")
    public void rollbackPortfolio(String request){
        System.out.println("Rolling back");
        RollbackEvent rollbackEvent = null;
        try{
            rollbackEvent = mapper.readValue(request, RollbackEvent.class);
            System.out.println(rollbackEvent);
            if(rollbackEvent.getTransactionType() == TransactionType.ASK)  this.portfolioService.rollbackPortfolio(rollbackEvent);
            else this.walletService.rollbackWalletValidation(rollbackEvent);
        }
        catch (Exception jx){
            log.error(jx.getMessage());
            if(jx instanceof JsonProcessingException) {
                /*TODO : IMPLEMENT JOURNALING */
            }
            this.addBackToKafka(rollbackEvent);
            this.sleep();
        }
    }

    private void addBackToKafka(RollbackEvent request) {
        try{
            this.kafkaTemplate.send("portfolio-validation-rollback", request);
        }
        catch (KafkaProducerException ex){
            /* TODO: IMPLEMENT LOGGING */
        }

    }

    private void sleep() {
        try{ Thread.sleep(10000); }
        catch (InterruptedException ignore){}
    }

}
