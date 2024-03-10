package com.exchange.riskmanagement.Model.Cache;


import lombok.Builder;
import lombok.ToString;

import java.io.Serializable;


@Builder
public record SymbolMetaCachable(

        //The key will be EXCHANGE:SYMBOL, ie NSE:AAPL
        String key, //15bytes
        Double liquidity, //8 bytes
        Double marketPrice //8 bytes


) implements Serializable {}

/* Nse has around 2190 symbols available for trading, therefor even if BSE has the same
   number available that would be 4380 of these objects cached

    4380 * (15bytes for the Redis HashKey + 31 for the Object) = 0.2 MB
*/