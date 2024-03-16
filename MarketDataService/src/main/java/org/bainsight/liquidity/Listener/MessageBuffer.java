package org.bainsight.liquidity.Listener;

import com.esotericsoftware.kryo.kryo5.io.Input;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lmax.disruptor.RingBuffer;
import org.bainsight.liquidity.Model.Events.TickEvent;
import org.exchange.library.Dto.MarketRelated.Tick;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Sinks;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

@Component
public class MessageBuffer {

    private final ExecutorService greenExecutor;
    private final Sinks.Many<String> messageSink;
    private final ObjectMapper mapper;
    private final RingBuffer<TickEvent> tickBuffer;

    MessageBuffer(final ExecutorService greenExecutor,
                  final @Qualifier("messageSink") Sinks.Many<String> messageSink,
                  final ObjectMapper mapper,
                  final RingBuffer<TickEvent> tickBuffer) {
        this.greenExecutor = greenExecutor;
        this.mapper = mapper;
        this.tickBuffer = tickBuffer;
        this.messageSink = messageSink;
    }


    public void offer(byte[] data, boolean isPrimary) {
        tickBuffer.publishEvent((event, seq) -> {
            event.setTick(getTick(data));
            event.setPrimary(isPrimary);
        });
    }

    private Tick getTick(byte[] data) {
        try { return mapper.readValue(data, Tick.class); }
        catch (IOException e) { return null; }
    }

}
