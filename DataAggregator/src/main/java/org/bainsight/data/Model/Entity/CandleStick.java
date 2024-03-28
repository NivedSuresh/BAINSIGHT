package org.bainsight.data.Model.Entity;

import com.redis.om.spring.annotations.Document;
import com.redis.om.spring.annotations.Indexed;
import com.redis.om.spring.annotations.Searchable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bainsight.data.Model.Dto.ExchangePrice;
import org.springframework.data.annotation.Id;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document
public class CandleStick {
    @Indexed
    @Searchable
    @Id
    private String symbol;
    private ZonedDateTime timeStamp;
    private double open;
    private double high;
    private double close;
    private double low;
    private double change;
    private long volume;
    private List<ExchangePrice> exchangePrices;


}