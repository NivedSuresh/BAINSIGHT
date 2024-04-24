package com.bainsight.risk.Mapper;

import org.bainsight.RiskRequest;
import org.bainsight.TransactionType;
import org.exchange.library.KafkaEvent.RollbackEvent;
import org.springframework.stereotype.Service;


@Service
public class Mapper {


    public RollbackEvent getRollbackEvent(RiskRequest request) {
        return RollbackEvent.builder()
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .symbol(request.getSymbol())
                .ucc(request.getUcc())
                .transactionType(request.getTransactionType() == TransactionType.BID ? org.exchange.library.Enums.TransactionType.BID: org.exchange.library.Enums.TransactionType.ASK)
                .build();
    }
}
