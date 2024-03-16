package org.bainsight.liquidity.Handler;

import com.lmax.disruptor.EventHandler;
import org.exchange.library.Dto.MarketRelated.Tick;


public class LockSnatcher implements EventHandler<Tick> {
    @Override
    public void onEvent(Tick event, long sequence, boolean endOfBatch) throws Exception {

    }
}
