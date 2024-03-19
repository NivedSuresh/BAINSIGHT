package org.bainsight.liquidity.Model.Events;

import com.lmax.disruptor.EventFactory;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class TickReceivedEvent extends TickEvent{
    public static EventFactory<TickReceivedEvent> TICK_RECEIVED_EVENT_FACTORY = TickReceivedEvent::new;
}
