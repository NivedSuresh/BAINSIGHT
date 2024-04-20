package org.exchange.library.Dto.MarketRelated;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Objects;


@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class Tick { //Estimated of 233Bytes

    // Can be used for recovery
    private long sequenceNumber;

    private String exchange;

    // Unique identifier for the instrument
    private String symbol;

    // Indicates if the instrument is currently tradable
    private boolean tradable;

    // Last traded price of the instrument
    private double lastTradedPrice;

    // Highest price traded during the period
    private double highPrice;

    // Lowest price traded during the period
    private double lowPrice;

    // Opening price of the instrument for the period
    private double openPrice;

    // Closing price of the instrument for the period
    private double closePrice;

    // Change in price from the previous close
    private double change;

    // Last traded quantity of the instrument
    private long lastTradedQuantity;

    // Average price of trades for the instrument
    private double averageTradePrice;

    private long volume;

    // Total volume traded for the instrument today
    private long volumeTradedToday;

    // Last traded time of the instrument
    private Instant lastTradedTime;

    // Timestamp of the tick data
    private Instant tickTimestamp;

    // Order book depth data (map of price levels to depth information)
    private MarketDepth marketDepth;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Tick tick = (Tick) object;
        return Objects.equals(symbol, tick.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol);
    }

    public String getKey(){
        return this.getExchange().concat(":").concat(this.getSymbol());
    }


}


