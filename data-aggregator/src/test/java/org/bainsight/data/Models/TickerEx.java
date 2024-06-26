package org.bainsight.data.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TickerEx{
    private String symbol;
    private double open, close,  high,  low, lastTradedPrice;
    private long volumeTradedToday;
}
