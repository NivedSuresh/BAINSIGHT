package org.bainsight.data.Simulation;

import lombok.Getter;
import org.exchange.library.Dto.MarketRelated.Depth;
import org.exchange.library.Dto.MarketRelated.MarketDepth;
import org.exchange.library.Dto.MarketRelated.Tick;
import org.jetbrains.annotations.NotNull;
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



    private static final AtomicLong nseSequenceGenerator = new AtomicLong(0);
    private static final AtomicLong bseSequenceGenerator = new AtomicLong(0);
    private final Queue<TickerEx> dequeB;
    private final Queue<TickerEx> dequeA;

    private final Random random = new Random();

    private final Map<String, List<Tick>> exchangeSpecificOrderBook;


    public MarketData() {
        this.dequeB = new ArrayBlockingQueue<>(100);
        this.dequeA = new ArrayBlockingQueue<>(100);
        this.exchangeSpecificOrderBook = new HashMap<>();
        this.exchangeSpecificOrderBook.put("NSE", new ArrayList<>());
        this.exchangeSpecificOrderBook.put("BSE", new ArrayList<>());
        insertSymbolsIntoQueues();
    }

    void insertSymbolsIntoQueues() {
        Set<String> tickers = getSymbols();

        tickers.forEach(s -> {
            double random = Math.random();
            double close = ThreadLocalRandom.current().nextDouble(100.0, 1000.0);
            double open = close + (getLosersForTheDay().contains(s) ? -random : random);
            double lastTradedPrice = close + (getLosersForTheDay().contains(s) ? -random : random);
            TickerEx tickerEx = new TickerEx(s, open, close, Double.MIN_VALUE, Double.MAX_VALUE, lastTradedPrice, 0);

            List<Tick> nse = this.exchangeSpecificOrderBook.get("NSE");
            List<Tick> bse = this.exchangeSpecificOrderBook.get("BSE");

            nse.add(null);
            bse.add(null);

            getDequeA().offer(tickerEx);
        });
    }

    @NotNull
    private static Set<String> getSymbols() {
        List<String> symbols = List.of(
                "AAPL", "MSFT", "GOOGL", "AMZN", "FB",
                "TSLA", "NVDA", "NFLX", "SBUX", "TWTR",
                "SNAP", "UBER", "LYFT", "TGT", "AMZN",
                "PYPL",  "META", "IBM", "ORCL", "BAIN"
        );

        return new HashSet<>(symbols);
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

    public MarketDepth getMarketDepth() {
        MarketDepth depth = new MarketDepth();
        List<Depth> buyDepth = new ArrayList<>();
        List<Depth> sellDepth = new ArrayList<>();

        for(int i=0 ; i<3 ; i++){
            buyDepth.add(new Depth());
            sellDepth.add(new Depth());
        }

        depth.setBuy(buyDepth);
        depth.setSell(sellDepth);

        return depth;
    }

    public Tick getRandomTick(TickerEx meta, PushTo pushTo, String exchange){

        double random = Math.random();

        double lastTradedPrice = meta.getLastTradedPrice() +
                (getLosersForTheDay().contains(meta.getSymbol()) ?
                        -(random / 10) :
                        (random < .6 ? random : -(Math.random())/2)
                );

        double change = lastTradedPrice - meta.getClose();

        long lastTradedQuantity = (long) (random * (random > .6 ? (random * 1000) : (random * 100)));

        double averageTradePrice = (meta.getLow() + meta.getHigh()) / 2;

        long volume = this.random.nextLong(5000, 1000000);

        Instant now = Instant.now();

        MarketDepth marketDepth = this.getMarketDepth();


        meta.setLow(Math.min(meta.getLow(), lastTradedPrice));
        meta.setLow(Math.min(meta.getLow(), meta.getOpen()));
        meta.setLow(Math.min(meta.getOpen(), meta.getLow()));

        meta.setHigh(Math.max(meta.getHigh(), lastTradedPrice));
        meta.setHigh(Math.max(meta.getHigh(), meta.getOpen()));
        meta.setHigh(Math.max(meta.getOpen(), meta.getHigh()));

        meta.setVolumeTradedToday(meta.getVolumeTradedToday() + lastTradedQuantity);
        meta.setLastTradedPrice(lastTradedPrice);

        Tick tick = new Tick();

        long sequence = exchange.equals("BSE") ? bseSequenceGenerator.incrementAndGet() : nseSequenceGenerator.incrementAndGet();

        tick.setSequenceNumber(sequence);
        tick.setExchange(exchange);
        tick.setSymbol(meta.getSymbol());
        tick.setTradable(true);
        tick.setLastTradedPrice(round(lastTradedPrice));
        tick.setHighPrice(round(meta.getHigh()));
        tick.setLowPrice(round(meta.getLow()));
        tick.setOpenPrice(round(meta.getOpen()));
        tick.setClosePrice(round(meta.getClose()));
        tick.setChange(round(change));
        tick.setLastTradedQuantity(lastTradedQuantity);
        tick.setAverageTradePrice(round(averageTradePrice));
        tick.setVolume(volume);
        tick.setVolumeTradedToday(meta.getVolumeTradedToday());
        tick.setLastTradedTime(now.minus((long) (random * 100), ChronoUnit.NANOS));
        tick.setTickTimestamp(now);
        tick.setMarketDepth(marketDepth);

        pushTo(meta, pushTo);

        return tick;
    }

    public Double round(double val){
        return Double.parseDouble(String.format("%.2f", val));
    }

    private void pushTo(TickerEx meta, PushTo pushTo) {
        if(pushTo == PushTo.QUEUE_A) dequeA.offer(meta);
        else if(pushTo == PushTo.QUEUE_B) dequeB.offer(meta);
    }




}
