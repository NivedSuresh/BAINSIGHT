package com.exchange.matchingengine.MarketDataSimulation.Simulation;

import com.exchange.matchingengine.MarketDataSimulation.Enums.PushTo;
import com.exchange.matchingengine.MarketDataSimulation.Models.TickerEx;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.exchange.library.Dto.MarketRelated.Depth;
import org.exchange.library.Dto.MarketRelated.MarketDepth;
import org.exchange.library.Dto.MarketRelated.Tick;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

@Getter
@Component
class MarketData {

    public static final AtomicLong aLong = new AtomicLong(0);
    private final Queue<TickerEx> dequeB;
    private final Queue<TickerEx> dequeA;


    MarketData() {
        this.dequeB = new ArrayBlockingQueue<>(1000);
        this.dequeA = new ArrayBlockingQueue<>(1000);
    }

    @PostConstruct
    void insertSymbolsIntoQueues() {
        List<String> tickers = List.of(
                "AAPL", "MSFT", "GOOGL", "AMZN", "FB", // Tech companies
                "JPM", "BAC", "WFC", "C", "GS",          // Banks
                "TSLA", "NVDA", "NFLX", "INTC", "AMD",   // Tech & entertainment
                "KO", "PEP", "MCD", "SBUX", "CMG",       // Food & beverages
                "DIS", "TWTR", "SNAP", "UBER", "LYFT",   // Entertainment & transportation
                "WMT", "TGT", "AMZN", "COST", "HD",      // Retail
                "BA", "LMT", "RTX", "GD", "NOC",         // Aerospace & defense
                "CVS", "WBA", "CI", "UNH", "ANTM",       // Healthcare
                "MMM", "CAT", "JNJ", "PFE", "MRK",       // Industrial & healthcare
                "GS", "MS", "C", "JPM", "BAC",           // Financial services
                "AMGN", "GILD", "BIIB", "REGN", "VRTX",  // Biotechnology & pharmaceuticals
                "V", "MA", "AXP", "PYPL", "SQ",          // Payment processing & fintech
                "SPY", "QQQ", "DIA", "IWM", "GLD",       // ETFs
                "XOM", "CVX", "BP", "TOT", "RDS.A",      // Oil & gas
                "IBM", "ORCL", "CRM", "SAP", "ADBE",     // Software & cloud computing
                "ATVI", "EA", "TTWO", "NTDOY", "UBSFF",  // Gaming
                "T", "VZ", "TMUS", "CHTR", "CMCSA",     // Telecommunications & media
                "INTU", "PAYX", "ADP", "FIS", "FISV",   // Financial technology
                "NOW", "CRM", "ZM", "SNOW", "TEAM"      // Cloud & software
        );

        tickers.forEach(s -> {
            double random = Math.random();
            double close = ThreadLocalRandom.current().nextDouble(10.0, 1000.0);
            double open = close + random > .6 ? random : -random;
            getDequeB().offer(new TickerEx(s, open, close, Double.MIN_VALUE, Double.MAX_VALUE, 0));
            getDequeA().offer(new TickerEx(s, open, close, Double.MIN_VALUE, Double.MAX_VALUE, 0));
        });
    }

    public Set<String> getLosersForTheDay() {
        return new HashSet<>(List.of(
                "MSFT", "QQQ", "DIA", "IWM",
                "PFE", "MRK", "NVDA", "AXP",
                "PYPL", "SQ", "DIS", "TWTR",
                "SNAP", "NOW", "CRM", "ZM",
                "CVS", "WBA", "CI", "UNH",
                "ANTM"));
    }

    public MarketDepth getMarketDepth(double close) {
        MarketDepth depth = new MarketDepth();
        List<Depth> buyDepth = new ArrayList<>();
        List<Depth> sellDepth = new ArrayList<>();

        for(int i=0 ; i<3 ; i++){
            long random = (long) (Math.random() * 10000);
            buyDepth.add(new Depth());
            sellDepth.add(new Depth());
        }

        depth.setBuy(buyDepth);
        depth.setSell(sellDepth);

        return depth;
    }

    public Tick getRandomTick(TickerEx meta, PushTo pushTo){

        double random = Math.random();

        double lastTradedPrice = meta.getClose() +
                (getLosersForTheDay().contains(meta.getSymbol()) ?
                        -(random / 10) :
                        (random < .6 ? random : -(Math.random())/2)
                );

        double change = lastTradedPrice - meta.getClose();

        long lastTradedQuantity = (long) (random * (random > .6 ? (random * 1000) : (random * 100)));

        double averageTradePrice = (meta.getLow() + meta.getHigh()) / 2;

        long volume = (long) (random  * 100000) * (random > .6 ? 10 : 1);

        Instant now = Instant.now();

        MarketDepth marketDepth = this.getMarketDepth(meta.getClose());


        meta.setLow(Math.min(lastTradedPrice, meta.getLow()));
        meta.setHigh(Math.max(meta.getHigh(), lastTradedPrice));
        meta.setVolumeTradedToday(meta.getVolumeTradedToday() + meta.getVolumeTradedToday());


        Tick tick = new Tick();

        tick.setSequenceNumber(aLong.incrementAndGet());
        tick.setExchange(getExchange());
        tick.setSymbol(meta.getSymbol());
        tick.setTradable(true);
        tick.setLastTradedPrice(lastTradedPrice);
        tick.setHighPrice(meta.getHigh());
        tick.setLowPrice(meta.getLow());
        tick.setOpenPrice(meta.getOpen());
        tick.setClosePrice(meta.getClose());
        tick.setChange(change);
        tick.setLastTradedQuantity(lastTradedQuantity);
        tick.setAverageTradePrice(averageTradePrice);
        tick.setVolume(volume);
        tick.setVolumeTradedToday(meta.getVolumeTradedToday());
        tick.setLastTradedTime(now.minus((long) (random * 100), ChronoUnit.NANOS));
        tick.setTickTimestamp(now);
        tick.setMarketDepth(marketDepth);

        pushTo(meta, pushTo);

        return tick;
    }

    private void pushTo(TickerEx meta, PushTo pushTo) {
        if(pushTo == PushTo.QUEUE_A) dequeA.offer(meta);
        else dequeB.offer(meta);
    }

    private String getExchange(){
        return aLong.get() % 2 == 0 ? "NSE" : "BSE";
    }


}
