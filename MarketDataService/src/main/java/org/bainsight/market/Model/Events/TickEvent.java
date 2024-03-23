package org.bainsight.market.Model.Events;

import lombok.*;
import org.exchange.library.Dto.MarketRelated.Tick;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TickEvent {
    private Tick tick;
    public void clear(){ this.tick = null; }
}
