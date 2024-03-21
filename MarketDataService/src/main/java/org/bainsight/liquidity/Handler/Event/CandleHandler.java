package org.bainsight.liquidity.Handler.Event;

import com.lmax.disruptor.EventHandler;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bainsight.liquidity.Config.Election.LeaderConfig;
import org.bainsight.liquidity.Handler.Persistance.CandleStickBuffer;
import org.bainsight.liquidity.Model.Dto.CandleStick;
import org.bainsight.liquidity.Model.Events.TickAcceptedEvent;
import org.exchange.library.Dto.MarketRelated.Tick;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;


@RequiredArgsConstructor
public class CandleHandler implements EventHandler<TickAcceptedEvent> {


    private final byte shard;
    private final byte totalHandlers;
    private final CandleStickBuffer candleStickBuffer;
    private final String[] profiles;

    @Override
    public void onEvent(TickAcceptedEvent event, long sequence, boolean endOfBatch) {

        System.out.println(event.getTick());

        if (!LeaderConfig.IS_LEADER.get() || sequence % totalHandlers != shard) return;
        Tick tick = event.getTick();
        CandleStick combinedStick = this.candleStickBuffer.updateAndGetCandleStick(tick);
    }



    /**
     * TODO: USE SCYLLA DB TO PERSIST THE SNAPSHOT
     * */
    public void takeSnapshot(){
        Map<String, CandleStick> sticks = this.candleStickBuffer.getSnapshot();
//        System.out.println(sticks.get("AAPL").getTimeStamp());
    }

    public CandleStickBuffer getCandleStickBuffer(){
        for(String profile: profiles){
            if(profile.equals("test")) return this.candleStickBuffer;
        }
        return null;
    }

}
