package org.bainsight.data.Listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lmax.disruptor.RingBuffer;
import org.bainsight.data.Model.Events.TickReceivedEvent;
import org.exchange.library.Dto.MarketRelated.Tick;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MessageReceiveBuffer {

    private static final int ORDER_BOOK_FETCH_RETRY_THRESHOLD = 2;
    private final ObjectMapper mapper;
    private final RingBuffer<TickReceivedEvent> receiverBuffer;

    @Value("${exchange.orderbook.url}")
    private String[] ORDER_BOOKS_URLS;

    MessageReceiveBuffer(final ObjectMapper mapper,
                         final RingBuffer<TickReceivedEvent> receiverBuffer)
    {
        this.mapper = mapper;
        this.receiverBuffer = receiverBuffer;

    }





    /**
     *  The messages are serialized and added to the RingBuffer.
     *  Messages are verified based on {ticker: last received sequence number}
     *  and are only persisted if:
     *  the newly received sequence number for the symbol:exchange
     *                         >
     *  last persisted sequence number for the symbol:exchange
     *
     *  ie:
     *      if {NSE:AAPL - 1987346 } > {NSE:AAPL - 1782906} PERSIST
     *      else IGNORE
     * */
    public void offer(byte[] data)
    {
        receiverBuffer.publishEvent((event, seq) -> {
            Tick tick = deserialize(data);
            if(tick == null) return;
            event.setTick(tick);
        });
    }

    /* TODO: CONVERT TO KYRO AS JACKSON IS EXTREMELY SLOW ACCORDING TO PROFILER */
    private Tick deserialize(byte[] bytes)
    {
        try{ return mapper.readValue(bytes, Tick.class); }
        catch (Exception e){ return null; }
    }


}
