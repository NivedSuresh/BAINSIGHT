package org.bainsight.market.Handler.Persistance;

import io.aeron.Aeron;
import org.bainsight.market.Model.Dto.CandleStick;
import org.bainsight.market.Model.Dto.ExchangePrice;
import org.bainsight.market.Model.Dto.VolumeWrapper;
import org.exchange.library.Dto.MarketRelated.Tick;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class CandleStickBuffer {


    /* VOLUME MAP CONSISTS OF SYMBOL -> {EXCHANGE:VOLUME}  PAIRS*/
    private final Map<String, List<VolumeWrapper>> volumeMap;

    /* Will persist CandleSticks as Symbol:Stick */
    private final Map<String, CandleStick> combinedSticks;
    private final AtomicBoolean lock = new AtomicBoolean(false);
    private final ZoneId zoneId;
    private final Aeron aeron;


    public CandleStickBuffer(final Aeron aeron)
    {
        this.aeron = aeron;
        this.combinedSticks = new HashMap<>();
        this.volumeMap =  new HashMap<>();
        this.zoneId = ZoneId.of("Asia/Kolkata");
    }


    /* TODO: FETCH ORDER BOOK ON CONSTRUCT */
    public  void fetchOrderBook(){

    }




    /** Will take a CandleStick from a specific exchange and returns
    a combined stick of all exchanges plus updates the current state **/
    public CandleStick updateAndGetCandleStick(final Tick tick) {

        while (lock.get()) ;
        lock.set(true);

        double lastTradedPrice = tick.getLastTradedPrice();
        long totalVolume = this.getUpdatedVolume(tick);

        ZonedDateTime tickTimeStamp = ZonedDateTime.ofInstant(tick.getTickTimestamp(), zoneId);


        CandleStick candleStick = this.combinedSticks.get(tick.getSymbol());
        List<ExchangePrice> exchangePrices = computePricesIfAbsentFromCandle(candleStick, tick);

        if (candleStick == null) {
            candleStick = CandleStick.builder()
                    .low(lastTradedPrice)
                    .high(lastTradedPrice)
                    /* open is updated when the candle stick for the symbol is created for the first time */
                    .open(lastTradedPrice)
                    .close(lastTradedPrice)
                    .symbol(tick.getSymbol())
                    .timeStamp(tickTimeStamp)
                    .volume(totalVolume)
                    .change(0.0)
                    .exchangePrices(exchangePrices)
                    .build();

        }
        else if (candleStick.getTimeStamp().isAfter(tickTimeStamp))
        {
           return null;
        }
        else
        {
            double low = Math.min(lastTradedPrice, candleStick.getLow());
            double high = Math.max(lastTradedPrice, candleStick.getHigh());
            double change = Math.round(lastTradedPrice - candleStick.getOpen());

            /* Reuse the object if exists, helps with Garbage Collection */
            candleStick.setLow(low);
            candleStick.setHigh(high);
            candleStick.setClose(lastTradedPrice);
            candleStick.setChange(change);
            candleStick.setVolume(totalVolume);
            candleStick.setTimeStamp(tickTimeStamp);
            candleStick.setExchangePrices(exchangePrices);
        }

        this.combinedSticks.put(tick.getSymbol(), candleStick);
        lock.set(false);
        return candleStick;
    }

    /* VOLUME MAP CONSISTS OF SYMBOL -> {EXCHANGE:VOLUME}  PAIRS*/
    private long getUpdatedVolume(Tick tick) {
        String exchange = tick.getExchange();
        String symbol = tick.getSymbol();
        List<VolumeWrapper> volumeWrappers = this.volumeMap.get(symbol);
        long totalVolume = 0;
        if(volumeWrappers == null)
        {
            volumeWrappers = new ArrayList<>();
            volumeWrappers.add(new VolumeWrapper(exchange, tick.getVolume()));
            this.volumeMap.put(symbol, volumeWrappers);
            totalVolume = tick.getVolume();
        }
        else
        {
            boolean found = false;
            for (VolumeWrapper volumeWrapper : volumeWrappers) {
                if (volumeWrapper.getExchange().equals(exchange)) {
                    found = true;
                    volumeWrapper.setVolume(tick.getVolume());
                }
                totalVolume += volumeWrapper.getVolume();
            }
            if (!found) {
                volumeWrappers.add(new VolumeWrapper(exchange, tick.getVolume()));
                totalVolume += tick.getVolume();
            }
        }
        return totalVolume;
    }

    private List<ExchangePrice> computePricesIfAbsentFromCandle(CandleStick candleStick, Tick tick)
    {
        List<ExchangePrice> exchangePrices = null;
        String forExchange = tick.getExchange();

        if(candleStick != null)
        {
            exchangePrices = candleStick.getExchangePrices();
            boolean found = false;
            for(ExchangePrice exchangePrice : exchangePrices) {
                String iterableExchange = exchangePrice.getExchange();
                if(iterableExchange.equals(forExchange)){
                    exchangePrice.setLastTradedPrice(tick.getLastTradedPrice());
                    found = true;
                    break;
                }
            }
            if(!found) exchangePrices.add(new ExchangePrice(tick.getExchange(), tick.getLastTradedPrice()));
        }
        else
        {
            exchangePrices = new ArrayList<>();
            exchangePrices.add(new ExchangePrice(tick.getExchange(), tick.getLastTradedPrice()));
        }
        return exchangePrices;
    }


    public Map<String, CandleStick> getSnapshot(boolean reset){
        while (lock.get());
        lock.set(true);
        /* Capture the snapshot */
        HashMap<String, CandleStick> clone = new HashMap<>(this.combinedSticks);
        /* Reset after capturing snapshot */
        if(reset)
        {
            this.reset();
        }
        lock.set(false);
        return clone;
    }



    // Clear the maps by 4pm every day as the market will be closed by 3:30
    public void reset(){
        this.combinedSticks.clear();
    }

}
