package org.bainsight.data.Persistance;


import jakarta.annotation.PostConstruct;
import org.bainsight.data.Exception.ExpiredTickTimeStampException;
import org.bainsight.data.Model.Dto.ExchangePrice;
import org.bainsight.data.Model.Dto.VolumeWrapper;
import org.bainsight.data.Model.Entity.CandleStick;
import org.bainsight.data.Repository.CandleStickRepo;
import org.exchange.library.Dto.MarketRelated.Tick;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class CandleStickBuffer {


    /* VOLUME MAP CONSISTS OF SYMBOL -> {EXCHANGE:VOLUME}  PAIRS*/
    private final Map<String, List<VolumeWrapper>> volumeMap;

    /* Will persist CandleSticks as CandleStick:Stick */
    private final Map<String, CandleStick> combinedSticks;
    private final Map<String, List<ExchangePrice>> exchangePriceMap;
    private final AtomicBoolean lock = new AtomicBoolean(false);
    private final ZoneId zoneId;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ExecutorService recoveryExecutor;
    private final ExecutorService greenExecutor;
    private final CandleStickRepo candleStickRepo;


    public CandleStickBuffer(final RedisTemplate<String, Object> redisTemplate,
                             final ExecutorService recoveryExecutor,
                             final ExecutorService greenExecutor,
                             final CandleStickRepo candleStickRepo)
    {
        this.redisTemplate = redisTemplate;
        this.recoveryExecutor = recoveryExecutor;
        this.greenExecutor = greenExecutor;
        this.candleStickRepo = candleStickRepo;
        this.combinedSticks = new HashMap<>();
        this.volumeMap =  new HashMap<>();
        this.exchangePriceMap = new HashMap<>();
        this.zoneId = ZoneId.of("Asia/Kolkata");
    }


    @PostConstruct
    public void restoreStateOnConstruct(){
        this.recoveryExecutor.execute(() -> {
            Set<String> keys = this.redisTemplate.keys("CandleStick:*");
            ZonedDateTime currentTime = ZonedDateTime.now();
            if(keys == null) return;
            keys.forEach(key -> this.fetchStickFromRedisAndUpdate(key, currentTime));
        });
    }

    private void fetchStickFromRedisAndUpdate(String key, ZonedDateTime now) {
        greenExecutor.execute(() -> {
            try{
                this.candleStickRepo.findById(key.substring(12))
                        .ifPresent(candleStick -> this.updateIfValid(candleStick, now));
            }
            catch (Exception e){
                /* TODO: JOURNALING */
            }
        });
    }

    private void updateIfValid(CandleStick stick, ZonedDateTime currentTime) {
        while (lock.get()) ;
        lock.set(true);

        if(this.combinedSticks.containsKey(stick.getSymbol())) return;

        try{ validateStickTimeStamp(stick, currentTime); }
        catch (ExpiredTickTimeStampException e){
            lock.set(false);
            return;
        }

        CandleStick existingStick = this.combinedSticks.get(stick.getSymbol());

        if(existingStick != null) stick = mergeRedisAndJvmSticks(stick, existingStick);

        this.combinedSticks.put(stick.getSymbol(), stick);

        lock.set(false);
    }

    private void validateStickTimeStamp(CandleStick stick, ZonedDateTime currentTime) {
        LocalDate currentDate = currentTime.toLocalDate();

        if(!currentDate.isEqual(stick.getTimeStamp().toLocalDate())) ExpiredTickTimeStampException.trigger();
        else if(currentTime.getHour() != stick.getTimeStamp().getHour()) ExpiredTickTimeStampException.trigger();
        else if (currentTime.getMinute() != stick.getTimeStamp().getMinute()) ExpiredTickTimeStampException.trigger();

    }


    private CandleStick mergeRedisAndJvmSticks(CandleStick stick, CandleStick existingStick) {
        existingStick.setLow(Math.min(stick.getLow(), existingStick.getLow()));
        existingStick.setHigh(Math.max(stick.getHigh(), existingStick.getHigh()));
        existingStick.setOpen(stick.getOpen());
        return existingStick;
    }


    /** Will take a CandleStick from a specific exchange and returns
    a combined stick of all exchanges plus updates the current state **/
    public CandleStick updateAndGetCandleStick(final Tick tick) {

        while (lock.get());

        lock.set(true);

        double lastTradedPrice = tick.getLastTradedPrice();
        long totalVolume = this.getUpdatedVolume(tick);

        ZonedDateTime tickTimeStamp = ZonedDateTime.ofInstant(tick.getTickTimestamp(), zoneId);


        CandleStick candleStick = this.combinedSticks.get(tick.getSymbol());
        List<ExchangePrice> exchangePrices = computePricesIfAbsentFromCandle(tick);

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

    private List<ExchangePrice> computePricesIfAbsentFromCandle(Tick tick)
    {
        List<ExchangePrice> exchangePrices = this.exchangePriceMap.get(tick.getSymbol());
        String forExchange = tick.getExchange();

        if(exchangePrices != null)
        {
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

        this.exchangePriceMap.put(tick.getSymbol(), exchangePrices);
        return exchangePrices;
    }


    public Map<String, CandleStick> getSnapshot(boolean reset){
        while (lock.get());

        lock.set(true);

        /* Capture the snapshot */
        Map<String, CandleStick> clone = new HashMap<>();
        for(String key : this.combinedSticks.keySet()){
            CandleStick stick = new CandleStick();
            CandleStick existing = this.combinedSticks.get(key);
            stick.setVolume(existing.getVolume());
            stick.setHigh(existing.getHigh());
            stick.setLow(existing.getLow());
            stick.setOpen(existing.getOpen());
            stick.setClose(existing.getClose());
            stick.setChange(existing.getChange());
            stick.setTimeStamp(existing.getTimeStamp());
            stick.setExchangePrices(existing.getExchangePrices());
            stick.setSymbol(existing.getSymbol());
            clone.put(key, stick);
        }
        /* Reset after capturing snapshot */
        if(reset)
        {
            this.reset();
        }
        lock.set(false);
        return clone;
    }


    public void reset(){
        this.combinedSticks.clear();
    }

}
