package org.bainsight.liquidity.Listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lmax.disruptor.RingBuffer;
import org.bainsight.liquidity.Model.Events.TickEvent;
import org.exchange.library.Dto.MarketRelated.Tick;
import org.springframework.stereotype.Component;

@Component
public class MessageBuffer {

    private final ObjectMapper mapper;
    private final RingBuffer<TickEvent> tickBuffer;

    MessageBuffer(final ObjectMapper mapper,
                  final RingBuffer<TickEvent> tickBuffer) {
        this.mapper = mapper;
        this.tickBuffer = tickBuffer;
    }


    public void offer(byte[] data) {
        tickBuffer.publishEvent((event, seq) -> {
            Tick tick = deserialize(data);
            if(tick == null) return;
            event.setTick(tick);
        });
    }

    /* TODO: CONVERT TO KYRO */
    private Tick deserialize(byte[] bytes) {
        try{ return mapper.readValue(bytes, Tick.class); }
        catch (Exception e){ return null; }
    }

}
