package com.bainsight.risk.Model.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.exchange.library.Dto.MarketRelated.ExchangePrice;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@RedisHash
public class CandleStick {

    @Id
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