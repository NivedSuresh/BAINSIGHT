package org.bainsight.liquidity.Handler.Persistance;

import org.bainsight.liquidity.Model.Dto.CandleStick;
import org.bainsight.liquidity.Model.Dto.ExchangeStick;
import org.exchange.library.Dto.MarketRelated.Tick;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class CandleStickBuffer {

    /* Will persist CandleStick info for all Exchanges */
    private final Map<String, List<ExchangeStick>> stickManager;

    /* Will persist common CandleStick info by aggregating data from all exchanges */
    private final Map<String, CandleStick> aggregatedSticks;
    private final AtomicBoolean lock = new AtomicBoolean(false);
    private final ZoneId zoneId;


    public CandleStickBuffer()
    {
        this.aggregatedSticks = new HashMap<>();
        this.stickManager = new HashMap<>();
        this.zoneId = ZoneId.of("Asia/Kolkata");
    }





    /** Will take a CandleStick from a specific exchange and returns
    a combined stick of all exchanges plus updates the current state **/
    public CandleStick updateAndGetCandleStick(final Tick tick)
    {

        while (lock.get());

        lock.set(true);

        /* Get all Sticks from all exchanges, if null then create a new empty List */
        List<ExchangeStick> sticks = stickManager.computeIfAbsent(
                tick.getSymbol(), k -> new ArrayList<>()
        );


        /* create the exchange stick out of the tick */
        ExchangeStick newExchangeStick = getExchangeStick(tick);

        int index = -1;
        for(int i=0 ; i<sticks.size() ; i++){
            if(sticks.get(i).getExchange().equals(tick.getExchange())){
                index = i;
                break;
            }
        }

        // if index is still -1, then this is the first update from the exchange so add it
        if(index == -1) sticks.add(newExchangeStick);
        else sticks.set(index, newExchangeStick);

        stickManager.put(tick.getSymbol(), sticks);

        CandleStick combinedStick = getUpdatedStick(sticks, tick);
        aggregatedSticks.put(tick.getSymbol(), combinedStick);

        lock.set(false);
        return combinedStick;
    }



    private CandleStick getUpdatedStick(List<ExchangeStick> sticks, Tick tick)
    {

        double low = Double.MAX_VALUE;
        double high = Double.MIN_VALUE;
        double open = Double.MAX_VALUE;
        double close = Double.MAX_VALUE;
        double change = Double.MAX_VALUE;
        long volume = 0;

        for(ExchangeStick stick : sticks){
            change = Math.min(change, stick.getChange());
            low = Math.min(low, stick.getLow());
            high = Math.max(high, stick.getHigh());
            open = Math.min(open, stick.getOpen());
            close = Math.min(close, stick.getClose());
            volume += stick.getVolume();
        }


        return CandleStick.builder()
                    .low(low)
                    .high(high )
                    .open(open)
                    .close(close)
                    .symbol(tick.getSymbol())
                    .timeStamp(ZonedDateTime.ofInstant(tick.getTickTimestamp(), zoneId))
                    .volume(volume)
                    .change(change)
                    .build();
    }


    public Map<String, CandleStick> getSnapshot(){
        while (lock.get());
        lock.set(true);
        /* Capture last minute's snapshot */
        HashMap<String, CandleStick> clone = new HashMap<>(this.aggregatedSticks);
        /* Reset after capturing snapshot */
        this.reset();
        lock.set(false);
        return clone;
    }



    private ExchangeStick getExchangeStick(Tick tick) {
        return ExchangeStick.builder()
                .exchange(tick.getExchange())
                .low(tick.getLowPrice())
                .high(tick.getHighPrice())
                .open(tick.getOpenPrice())
                .close(tick.getClosePrice())
                .volume(tick.getVolume())
                .change(tick.getChange())
                .build();
    }


    // Clear the maps by 4pm every day as the market will be closed by 3:30
    public void reset(){
        this.stickManager.clear();
        this.aggregatedSticks.clear();
    }

}
