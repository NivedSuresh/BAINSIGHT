package org.bainsight.watchlist.CandleStick.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
class ExchangePrice {
    private String exchange;
    private double lastTradedPrice;
}
