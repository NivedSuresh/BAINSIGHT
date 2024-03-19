package org.bainsight.liquidity.Handler.Event;

import com.lmax.disruptor.EventHandler;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bainsight.liquidity.Handler.Persistance.CandleManagerBuffer;
import org.bainsight.liquidity.Model.Dto.CandleStick;
import org.bainsight.liquidity.Model.Events.TickAcceptedEvent;
import org.exchange.library.Dto.MarketRelated.Tick;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;


/* TODO: IMPLEMENT event.clear() IF NEEDED.
 *
 *       CONSIDERATIONS:
 *       1) HANDLER IS SHARDED.
 *       2) BUFFER SIZE:1024, WILL GET OVERWRITTEN IN MICRO SECONDS ANYWAY
 *          WHILE THE UPDATES ARE FLOWING.
 *
 *        ?? MIGHT HAVE TO IMPLEMENT CLEARING THE BUFFER WITH A CRON JOB SO
 *              THE MEMORY IS FREE DURING HOLIDAYS/MARKET CLOSED HOURS. ??
 *
 *  */

@RequiredArgsConstructor
public class CandleHandler implements EventHandler<TickAcceptedEvent> {


    private final byte shard;
    private final byte totalHandlers;
    private final RedisTemplate<String, Object> redisTemplate;
    private final CandleManagerBuffer candleManagerBuffer;

    @Override
    public void onEvent(TickAcceptedEvent event, long sequence, boolean endOfBatch) {
        if (sequence % totalHandlers != shard) return;
        Tick tick = event.getTick();

    }


    // TODO: IMPLEMENT DISTRIBUTED CRONJOB OR CONSENSUS USING ZOOKEEPER
    @Scheduled()
    public void enableTakeSnapshot(){
        Map<String, CandleStick> sticks = this.candleManagerBuffer.getSnapshot();
    }


}
