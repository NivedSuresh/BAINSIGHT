package org.bainsight.liquidity.Model.Events;

import com.lmax.disruptor.EventFactory;
import lombok.*;


@NoArgsConstructor
@Getter
@Setter
@ToString
public class TickAcceptedEvent extends TickEvent{
    private String key;
    public static EventFactory<TickAcceptedEvent> TICK_ACCEPTED_EVENT_FACTORY = TickAcceptedEvent::new;
}
