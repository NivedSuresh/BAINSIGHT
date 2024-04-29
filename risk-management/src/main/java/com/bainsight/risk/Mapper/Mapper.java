package com.bainsight.risk.Mapper;

import com.bainsight.risk.Model.Entity.CandleStick;
import org.bainsight.RiskRequest;
import org.bainsight.TransactionType;
import org.exchange.library.Dto.Symbol.CandleStickDto;
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


    public CandleStickDto toCandleStickDto(CandleStick stick){
        return CandleStickDto.builder()
                .symbol(stick.getSymbol())
                .timeStamp(stick.getTimeStamp().toLocalDateTime())
                .low(stick.getLow())
                .high(stick.getHigh())
                .open(stick.getOpen())
                .close(stick.getClose())
                .change(stick.getChange())
                .volume(stick.getVolume())
                .exchangePrices(stick.getExchangePrices())
                .build();
    }
}
