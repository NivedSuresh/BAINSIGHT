package org.bainsight.liquidity.Model.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CandleStick {
    private String symbol;
    private ZonedDateTime timeStamp;
    private Double open;
    private Double high;
    private Double close;
    private Double low;
    private Double change;
    private Long volume;
    private List<ExchangePrice> exchangePrices;
}