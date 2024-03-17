package org.bainsight.liquidity.Handler;

import com.lmax.disruptor.EventHandler;
import gnu.trove.set.hash.TLongHashSet;
import org.bainsight.liquidity.Listener.MessageRangeBuffer;
import org.bainsight.liquidity.Model.Events.TickEvent;
import org.exchange.library.Dto.MarketRelated.Tick;
import org.springframework.stereotype.Component;

@Component
public class TickEventHandler implements EventHandler<TickEvent> {


    private final MessageRangeBuffer rangeBuffer;
    private long sequence;

    /* TODO: SCHEDULE CLEARING */
    private final TLongHashSet missed;

    public TickEventHandler(MessageRangeBuffer rangeBuffer) {
        this.rangeBuffer = rangeBuffer;
        missed = new TLongHashSet();
        this.sequence = 0;
    }


    /* TODO : REMOVE PRIMARY FIELD FROM TICK_EVENT
         AS IT WAS ONLY ADDED FOR TESTING */
    @Override
    public void onEvent(TickEvent event, long seq, boolean endOfBatch) {
        Tick tick = event.getTick();
        String key = tick.getKey();
        missed.remove(tick.getSequenceNumber());
        boolean put = rangeBuffer.put(key, tick.getSequenceNumber());
        long expected = this.sequence + 1;
        long received = tick.getSequenceNumber();
        if(expected < received){
            markOutOfOrder(expected, received);
        }
        updateLastReceived(received);
        event.releaseEvent();
    }

    private void updateLastReceived(long received) {
       sequence = Math.max(received, sequence);
    }

    private void markOutOfOrder(long expected, long received) {
        while (expected < received){
            missed.add(expected++);
        }
        System.out.println(missed.size());
    }

}
