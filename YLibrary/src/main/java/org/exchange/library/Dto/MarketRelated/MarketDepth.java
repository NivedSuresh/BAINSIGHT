package org.exchange.library.Dto.MarketRelated;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarketDepth {
    public List<Depth> buy;
    public List<Depth> sell;
}
