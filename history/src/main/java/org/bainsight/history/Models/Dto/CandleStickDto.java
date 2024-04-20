package org.bainsight.history.Models.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CandleStickDto {
    private String symbol;
    private LocalDateTime timeStamp;
    private double open;
    private double high;
    private double close;
    private double low;
    private double change;
    private long volume;
}
