package org.bainsight.history.Mapper;

import org.bainsight.history.Models.Dto.CandleStickDto;
import org.bainsight.history.Models.Entity.CandleStickEntity;
import org.springframework.stereotype.Service;


@Service
public class Mapper {

    public CandleStickDto toCandleStickDto(CandleStickEntity stick){
        return CandleStickDto.builder()
                .symbol(stick.getKey().getSymbol())
                .timeStamp(stick.getKey().getTimestamp())
                .low(stick.getLow())
                .high(stick.getHigh())
                .open(stick.getOpen())
                .close(stick.getClose())
                .change(stick.getChange())
                .volume(stick.getVolume())
                .build();
    }

}
