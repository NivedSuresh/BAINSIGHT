package org.bainsight.data.Model.Events;

import com.lmax.disruptor.EventFactory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@NoArgsConstructor
@Getter
@Setter
@ToString
public class TickAcceptedEvent extends TickEvent {
    private String key;
    public static EventFactory<TickAcceptedEvent> TICK_ACCEPTED_EVENT_FACTORY = TickAcceptedEvent::new;
}
