package org.bainsight.liquidity.Handler.Event;


import com.lmax.disruptor.EventHandler;
import lombok.AllArgsConstructor;
import org.bainsight.liquidity.Model.Events.TickAcceptedEvent;
import org.exchange.library.Dto.MarketRelated.Tick;


/* TODO: IMPLEMENT event.clear() IF NEEDED.
 *
 *       CONSIDERATIONS:
 *       1) HANDLER IS SHARDED.
 *       2) BUFFER SIZE:1024, WILL GET OVERRIDDEN IN NANO SECONDS ANYWAY
 *          WHILE THE UPDATES ARE FLOWING.
 *
 *        ?? MIGHT HAVE TO IMPLEMENT CLEARING THE BUFFER WITH A CRON JOB SO
 *              THE MEMORY IS FREE DURING HOLIDAYS/MARKET CLOSED HOURS. ??
 *
 *  */


@AllArgsConstructor
public class MarketAnalyzer implements EventHandler<TickAcceptedEvent> {


    private final byte shard;
    private final byte totalHandlers;

    @Override
    public void onEvent(TickAcceptedEvent event, long sequence, boolean endOfBatch) {
        if (sequence % totalHandlers != shard) return;
        Tick tick = event.getTick();
    }
}
