package org.bainsight.history.Data;


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bainsight.history.Config.CMD.TimeStamp;
import org.bainsight.history.Mapper.Mapper;
import org.bainsight.history.Models.Dto.CandleStick;
import org.bainsight.history.Models.Dto.CandleStickDto;
import org.bainsight.history.Models.Entity.CandleStickEntity;
import org.exchange.library.Exception.BadRequest.InvalidStateException;
import org.exchange.library.Exception.BadRequest.InvalidTimeSpaceException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class HistoryServiceImpl {

    private final HistoryRepo historyRepo;
    private final HazelcastInstance instance;
    private final Mapper mapper;
    private final TimeStamp timeStamp;
    private static final String CURRENT_DAY = "1D";
    private static final String CURRENT_WEEK = "1W";
    private static final String CURRENT_MONTH = "1M";
    private static final String CURRENT_YEAR = "1Y";
    private static final String THREE_YEAR = "3Y";


    public void saveCandleStick(CandleStick stick){

        CandleStickEntity.Key key = new CandleStickEntity.Key(stick.getSymbol(),
                stick.getTimeStamp().toLocalDateTime());

        CandleStickEntity entity = CandleStickEntity.builder()
                .key(key)
                .low(stick.getLow())
                .high(stick.getHigh())
                .close(stick.getClose())
                .open(stick.getOpen())
                .change(stick.getChange())
                .volume(stick.getVolume())
                .build();


        this.historyRepo.insert(entity).subscribe(
                candleStick -> {},
                throwable -> log.error(throwable.getMessage())
        );
    }


    public void saveCandleStick(CandleStickEntity candleStick){
        this.historyRepo.insert(candleStick)
                .subscribe(stick -> {}, throwable -> log.error(throwable.getMessage()));
    }

    public Flux<CandleStickDto> fetchSticksForTheDay(String symbol){

        final String key = getKey("1D", symbol);
        IMap<String, List<CandleStickDto>> cache = this.instance.getMap("candle_sticks");

        List<CandleStickDto> entities = Objects.requireNonNullElseGet(cache.get(key), ArrayList::new);
        if(!entities.isEmpty()) return Flux.fromIterable(entities);

        LocalDateTime now = this.getLatestSnapshotDateAndTime();

        LocalDateTime start = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 9, 0, 0, 0);
        if(start.getDayOfWeek() == DayOfWeek.SUNDAY)
        {
            start = start.minusDays(1);
        }


        return this.historyRepo.currentDaySticks(symbol, start)
                .map(mapper::toCandleStickDto)
                .doOnNext(entities::add)
                .doOnComplete(() -> {
                    System.out.println(entities.size());
                    Duration ttl = getTTl(now, 5);
                    long seconds = ttl.getSeconds();
                    cache.put(key, entities, seconds, TimeUnit.SECONDS);
                    log.info("Sticks for the day cached till {}", now.plus(ttl));
                });
    }

    private LocalDateTime getLatestSnapshotDateAndTime() {
        LocalDateTime now = LocalDateTime.now();

        if(now.getDayOfWeek() == DayOfWeek.SUNDAY){
            now = now.minusDays(1);
        }

        if(now.getHour() < 9) {
            now = LocalDateTime.of(
                    now.toLocalDate().minusDays(1),
                    LocalTime.of(15, 30, 0)
            );
        }

        else if((now.getHour() == 15 && now.getMinute() > 30 ) || (now.getHour() > 15)){
            now = LocalDateTime.of(
                    now.toLocalDate(),
                    LocalTime.of(15, 30, 0)
            );
        }

        return now;
    }


    private Duration getTTl(LocalDateTime now, int minutes)
    {
        /* *
        *  If minutes > 120 then the value has to be >= 3days, meaning you can cache
        *  this data till next day 9:00 and there won't be any change for the data anymore today.
        * */
        if(minutes > 120) return getTTLForMarketClosed(now);

        boolean isMarketClosed = (now.getHour() > 15 && now.getMinute() > 30) || (now.getHour() < 9);

        return isMarketClosed ? getTTLForMarketClosed(now) : getDurationForMarketOpen(now, minutes);
    }


    private Duration getDurationForMarketOpen(LocalDateTime now, int minute) {
        int currentMinute = now.getMinute() % minute;
        int secondsLeft = ((minute - currentMinute) * 60) - now.getSecond();
        return Duration.ofSeconds(secondsLeft);
    }


    private Duration getTTLForMarketClosed(LocalDateTime now) {
        LocalDateTime marketOpen;

        if(now.getHour() < 9) marketOpen = LocalDateTime.of(now.toLocalDate(), LocalTime.of(9, 0, 0));
        else marketOpen = LocalDateTime.of(now.plusDays(1).toLocalDate(), LocalTime.of(9, 0, 0));

        return Duration.between(now, marketOpen);
    }


    public Flux<CandleStickDto> fetchByTime(final String symbol, final String timeSpace) {
        List<LocalDateTime> times = findTimesStampsByTimeSpace(timeSpace);

        final String key = this.getKey(timeSpace, symbol);

        IMap<String, List<CandleStickDto>> cache = this.instance.getMap("candle_sticks");

        List<CandleStickDto> entities = Objects.requireNonNullElseGet(cache.get(key), ArrayList::new);
        if(!entities.isEmpty()) return Flux.fromIterable(entities);

        return Flux.fromIterable(times)
                .concatMap(time -> this.historyRepo.findByTimeStamp(time, symbol))
                .map(mapper::toCandleStickDto)
                .concatMap(candleStick -> {
                    int prevIndex = entities.size() - 1;

                    if(prevIndex >= 0)
                    {
                        CandleStickDto prev = entities.get(prevIndex);

                        double change = candleStick.getClose() - prev.getClose();
                        BigDecimal bigChange = new BigDecimal(change).setScale(2, RoundingMode.DOWN);
                        change = bigChange.doubleValue();

                        candleStick.setChange(change);

                        Mono<CandleStickEntity> highForSymbolBwTimestamp = this.historyRepo.findHighForSymbolBwTimestamp(prev.getTimeStamp(), candleStick.getTimeStamp(), symbol);
                        Mono<CandleStickEntity> lowForSymbolBwTimestamp = this.historyRepo.findLowForSymbolBwTimestamp(prev.getTimeStamp(), candleStick.getTimeStamp(), symbol);

                        return Mono.zip(Mono.just(candleStick), highForSymbolBwTimestamp, lowForSymbolBwTimestamp);
                    }
                    else
                    {
                        CandleStickEntity high = new CandleStickEntity();
                        high.setHigh(candleStick.getHigh());
                        CandleStickEntity low = new CandleStickEntity();
                        low.setLow(candleStick.getLow());
                        return Mono.zip(Mono.just(candleStick), Mono.just(high), Mono.just(low));
                    }

                })
                .doOnNext(tuple -> {

                    CandleStickDto persistable = tuple.getT1();
                    CandleStickEntity high = tuple.getT2();
                    CandleStickEntity low = tuple.getT3();

                    persistable.setHigh(high.getHigh());
                    persistable.setLow(low.getLow());

                    entities.add(persistable);
                })
                .map(Tuple2::getT1)
                .doOnComplete(() -> {
                    int minutes = fetchMinutesByTimeSpace(timeSpace);
                    Duration ttl = getTTl(LocalDateTime.now(), minutes);
                    long seconds = ttl.getSeconds();
                    cache.put(key, entities, seconds , TimeUnit.SECONDS);
                })
                .doOnError(throwable -> log.error(throwable.getMessage()));
    }

    private String getKey(String timeSpace, String symbol) {
        switch (timeSpace){
            case CURRENT_DAY -> { return "1D:" + symbol; }
            case CURRENT_WEEK -> { return "1W:" + symbol; }
            case CURRENT_MONTH -> { return "1M:" + symbol; }
            case CURRENT_YEAR -> { return "1Y:" + symbol; }
            case THREE_YEAR ->  { return "3Y:" + symbol; }
            default -> throw new InvalidStateException();
            }
    }



    private int fetchMinutesByTimeSpace(String timeSpace) {
        switch (timeSpace){
            case CURRENT_DAY -> { return 5; }
            case CURRENT_WEEK -> { return 30; }
            case CURRENT_MONTH -> { return 120; }
            case CURRENT_YEAR -> { return 4320; }
            case THREE_YEAR -> { return 21_600; }
            default -> throw new InvalidTimeSpaceException();
        }
    }

    private List<LocalDateTime> findTimesStampsByTimeSpace(String timeSpace) {
        switch (timeSpace){
            case CURRENT_WEEK -> { return timeStamp._1W; }
            case CURRENT_MONTH -> { return timeStamp._1M; }
            case CURRENT_YEAR -> { return timeStamp._1Y; }
            case THREE_YEAR -> { return timeStamp._3Y; }
            default -> throw new InvalidTimeSpaceException();
        }
    }


    @PostConstruct
    public void cacheLosersGainersForTheDay() {

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime latest = getLatest(now);


        Mono<CandleStickEntity> loserChange = this.historyRepo.findMinChangeByTimestamp(latest);
        Mono<CandleStickEntity> gainerChange = this.historyRepo.findMaxChangeByTimestamp(latest);

        Mono<Tuple2<CandleStickEntity, CandleStickEntity>> combinedChange = Mono.zip(loserChange, gainerChange);

        final LocalDateTime lt = latest;

        Mono<List<CandleStickDto>> combinedMono = combinedChange.flatMap(changes -> {
            CandleStickEntity lc = changes.getT1();
            CandleStickEntity gc = changes.getT2();

            if(lc.getChange() == null || gc.getChange() == null) return Mono.error(InvalidStateException::new);

            Mono<CandleStickEntity> loser = this.historyRepo.findCandleStickEntityByChangeAndTimeStamp(lc.getChange(), lt);
            Mono<CandleStickEntity> gainer = this.historyRepo.findCandleStickEntityByChangeAndTimeStamp(gc.getChange(), lt);
            return Mono.zip(loser, gainer);
        })
        .map(entities -> {
            CandleStickDto loser = mapper.toCandleStickDto(entities.getT1());
            CandleStickDto gainer = mapper.toCandleStickDto(entities.getT2());

            List<CandleStickDto> combinedList = new ArrayList<>();
            combinedList.add(loser);
            combinedList.add(gainer);
            return combinedList;
        })
        .doOnError(throwable -> log.error("Failed preparing Loser/Gainer to be cached!"));

        combinedMono.subscribe(combinedList -> {
            IMap<String, List<CandleStickDto>> candleSticks = instance.getMap("candle_sticks");
            /* No need to have a ttl as the job gets executed each 5 minutes until the market is closed */
            candleSticks.put("losers_gainers", combinedList);
        });

    }

    public static LocalDateTime getLatest(LocalDateTime now) {
        int min = now.getMinute() - now.getMinute() % 5;

        LocalDateTime latest = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), Math.min(now.getHour(), 15), min, 0, 0);

        if(latest.getHour() > 15 || (latest.getHour() == 15 && latest.getMinute() > 30 )){
            latest = LocalDateTime.of(now.toLocalDate(), LocalTime.of(15, 30, 0, 0));
        }
        else if(latest.getHour() < 9) {
            latest = LocalDateTime.of(now.toLocalDate().minusDays(1) , LocalTime.of(15, 30, 0));
        }

        return latest;
    }


    public List<CandleStickDto> findLosersGainersForTheDay() {
        IMap<String, List<CandleStickDto>> candleSticks = instance.getMap("candle_sticks");
        return candleSticks.get("losers_gainers");
    }

    public CandleStickEntity fetchLatestTimeStamp(String symbol){
//        this.historyRepo.deleteByTimestamp(LocalDateTime.now().minusDays(2)).block();
        return this.historyRepo.findLatestTimestamp(symbol).block();
    }

    public CandleStickEntity findByKey(CandleStickEntity.Key key) {
        return this.historyRepo.findByKey(key.getSymbol(), key.getTimestamp()).block();
    }

}
