package org.bainsight.data.Models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class TestStick {
    private double open;
    private double high;
    private double low;
    private double close;
    private long volume;
    private double change;
    private ZonedDateTime timeStamp;
}
