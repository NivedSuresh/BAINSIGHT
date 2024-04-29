package org.exchange.library.Dto.Symbol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.exchange.library.Dto.MarketRelated.ExchangePrice;

import java.time.LocalDateTime;
import java.util.List;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CandleStickDto {
    private String symbol;
    private LocalDateTime timeStamp;
    private double open;
    private double high;
    private double close;
    private double low;
    private double change;
    private long volume;
    private List<ExchangePrice> exchangePrices;
}
