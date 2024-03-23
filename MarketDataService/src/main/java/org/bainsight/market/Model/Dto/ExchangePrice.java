package org.bainsight.market.Model.Dto;


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
