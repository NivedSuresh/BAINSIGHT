package org.bainsight.history.Data;


import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bainsight.history.Models.Dto.CandleStick;
import org.bainsight.history.Models.Entity.CandleStickEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class HistoryServiceImpl {

    private final HistoryRepo historyRepo;

    public void saveCandleStick(CandleStick stick){
        try{

            CandleStickEntity.Key key = new CandleStickEntity.Key(stick.getSymbol(),
                                            stick.getTimeStamp().toLocalDateTime());

            CandleStickEntity entity = CandleStickEntity.builder()
                    .key(key)
                    .low(stick.getLow())
                    .high(stick.getHigh())
                    .close(stick.getClose())
                    .open(stick.getOpen())
                    .change(stick.getChange())
                    .volume(stick.getVolume())
                    .build();

            this.historyRepo.insert(entity).subscribe(
                    candleStick -> {},
                    throwable -> log.error(throwable.getMessage())
            );
        }
        catch (Exception e){
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
        }
    }


    public void saveCandleStick(CandleStickEntity candleStick){
        this.historyRepo.insert(candleStick)
                .subscribe(
                        stick -> {},
                        throwable -> log.error(throwable.getMessage())
                );
    }



}
