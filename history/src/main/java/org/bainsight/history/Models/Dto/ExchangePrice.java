package org.bainsight.history.Models.Dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.UserDefinedType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExchangePrice {
    private String exchange;
    private double lastTradedPrice;
}
