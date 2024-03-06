package org.bainsight.liquidity.Model.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CandleStick {
    private String tradingSymbol;
    public LocalDateTime timeStamp;
    private Double open;
    private Double high;
    private Double close;
    private Double low;
    private Long volume;
}
/*

**Market Data Processing with Redis Cluster and NoSQL Aggregation**

This script outlines a process for handling market data updates from exchanges (NSE/BSE) for a broker that's not co-located.

        **Assumptions:**

        * Approximately 2200 tradable tickers on NSE (more than BSE).
        * Exchange sends updates every second (may vary in reality).
        * Not all updates need permanent storage.

        **Proposed Approach:**

        1. **Temporary Storage with Redis Cluster:**
        * Each market update is stored in a Redis Cluster with a 10-minute Time-To-Live (TTL).
        * This provides temporary storage while filtering for valuable data.
        * Estimated size per update: 80 bytes (object + key).

        2. **Cron Job for Aggregation:**
        * A cron job runs every 10 minutes to aggregate data from Redis.
        * This aggregation could involve:
        * Creating new candlestick objects representing the last 10 minutes.
        * Calculating relevant market insights (e.g., symbol deviation within the last hour).

        3. **Further Aggregation with NoSQL:**
        * The aggregated data (e.g., candlesticks) is persisted in a NoSQL database for further analysis and historical tracking.

**Benefits:**

        * Efficient temporary storage with TTL in Redis.
        * Flexible aggregation and insight generation using cron jobs.
        * Scalable storage of historical data in a NoSQL database.

**Note:**

        * This is a high-level overview. Actual implementation details and data sizes may vary.

        **Further Considerations:**

        * Data filtering strategies for selecting valuable updates in Redis.
        * Error handling and data consistency across different storage layers.

*/