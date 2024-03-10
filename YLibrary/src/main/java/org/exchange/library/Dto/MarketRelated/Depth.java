package org.exchange.library.Dto.MarketRelated;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Depth {
    private int quantity;
    private double price;
    private int orders;
}
