package org.bainsight.liquidity.Model.Events;

import com.lmax.disruptor.EventFactory;
import lombok.Data;
import org.exchange.library.Dto.MarketRelated.Tick;

@Data
public class TickEvent {
    private Tick tick;

    public TickEvent() {}

    public void releaseEvent(){
        this.tick = null;
    }
    public static EventFactory<TickEvent> TICK_EVENT_FACTORY = TickEvent::new;
}
