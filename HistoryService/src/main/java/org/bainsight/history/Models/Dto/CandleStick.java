package org.bainsight.history.Models.Dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CandleStick {

    private String symbol;
    private ZonedDateTime timeStamp;
    private double open;
    private double high;
    private double close;
    private double low;
    private double change;
    private long volume;
    private List<ExchangePrice> exchangePrices;


}