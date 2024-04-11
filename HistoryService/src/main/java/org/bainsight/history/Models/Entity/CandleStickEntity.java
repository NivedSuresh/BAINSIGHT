package org.bainsight.history.Models.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.*;

import java.io.Serializable;
import java.time.LocalDateTime;


@Table("candle_sticks")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CandleStickEntity {

    @PrimaryKeyClass
    @AllArgsConstructor
    @Data
    @NoArgsConstructor
    public static class Key implements Serializable {
        @PrimaryKeyColumn(name = "symbol", ordinal = 0, type = PrimaryKeyType.PARTITIONED, ordering = Ordering.ASCENDING)
        private String symbol;

        @PrimaryKeyColumn(name = "timestamp", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.ASCENDING)
        private LocalDateTime timestamp;

    }

    @PrimaryKey
    private Key key;
    private double open;
    private double high;
    private double close;
    private double low;
    private double change;
    private long volume;

}

