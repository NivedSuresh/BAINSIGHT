package org.bainsight.liquidity.Model.Events;

import com.lmax.disruptor.EventFactory;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.exchange.library.Dto.MarketRelated.Tick;

@Data
public class TickEvent {
    private Tick tick;
    private boolean isPrimary;

    public TickEvent() {}

    public void releaseEvent(){
        this.tick = null;
    }
    public static EventFactory<TickEvent> TICK_EVENT_FACTORY = TickEvent::new;
}
