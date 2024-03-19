package org.bainsight.liquidity.Model.Events;

import lombok.*;
import org.exchange.library.Dto.MarketRelated.Tick;

import java.util.concurrent.atomic.AtomicLong;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TickEvent {
    private Tick tick;
    public void clear(){ this.tick = null; }
}
