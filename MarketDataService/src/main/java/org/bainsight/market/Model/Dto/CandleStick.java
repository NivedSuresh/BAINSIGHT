package org.bainsight.market.Model.Dto;

import com.redis.om.spring.annotations.Indexed;
import com.redis.om.spring.annotations.Searchable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@RedisHash
public class CandleStick {
    @Indexed
    @Searchable
    @Id
    private String symbol;
    private ZonedDateTime timeStamp;
    private Double open;
    private Double high;
    private Double close;
    private Double low;
    private Double change;
    private Long volume;
    private List<ExchangePrice> exchangePrices;

}