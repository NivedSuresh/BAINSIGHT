package org.bainsight.liquidity.Model.Dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExchangeStick {
    private String exchange;
    private double lastTradedPrice;
    private Double open;
    private Double high;
    private Double close;
    private Double low;
    private Double change;
    private Long volume;
}
