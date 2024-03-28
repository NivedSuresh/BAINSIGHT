package org.bainsight.data.Model.Events;

import com.lmax.disruptor.EventFactory;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.ByteArrayInputStream;

@NoArgsConstructor
@Data
public class MessageEvent {
    private ByteArrayInputStream inputStream;
    public void release(){
        this.inputStream = null;
    }

    public static EventFactory<MessageEvent> MESSAGE_EVENT_FACTORY = MessageEvent::new;
}
