package org.exchange.library.Dto.MarketRelated;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


public record Tick(

        // Unique identifier for the instrument
        String symbol,

        // Indicates if the instrument is currently tradable
        boolean tradable,

        // Last traded price of the instrument
        double lastTradedPrice,

        // Highest price traded during the period
        double highPrice,

        // Lowest price traded during the period
        double lowPrice,

        // Opening price of the instrument for the period
        double openPrice,

        // Closing price of the instrument for the period
        double closePrice,

        // Change in price from the previous close
        double change,

        // Last traded quantity of the instrument
        long lastTradedQuantity,

        // Average price of trades for the instrument
        double averageTradePrice,

        // Total volume traded for the instrument today
        long volumeTradedToday,

        // Total buy quantity for the instrument
        double totalBuyQuantity,

        // Total sell quantity for the instrument
        double totalSellQuantity,

        // Last traded time of the instrument
        LocalDateTime lastTradedTime,

        // Open interest (total outstanding contracts)
        double oi,

        // Highest open interest for the day
        double oiDayHigh,

        // Lowest open interest for the day
        double oiDayLow,

        // Timestamp of the tick data
        LocalDateTime tickTimestamp,

        // Order book depth data (map of price levels to depth information)
        MarketDepth depth

) {
}

