package org.exchange.library.Dto.MarketRelated;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExchangePrice {
    private String exchange;
    private double lastTradedPrice;
}
