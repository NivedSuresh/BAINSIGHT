package org.bainsight.history.Data;


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bainsight.history.Config.CMD.TimeStamp;
import org.bainsight.history.Mapper.Mapper;
import org.bainsight.history.Models.Dto.CandleStick;
import org.bainsight.history.Models.Dto.CandleStickDto;
import org.bainsight.history.Models.Entity.CandleStickEntity;
import org.exchange.library.Exception.BadRequest.InvalidTimeSpaceException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

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


        LocalDateTime now = LocalDateTime.now();

        LocalDateTime start = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 9, 0, 0, 0);
        if(start.getDayOfWeek() == DayOfWeek.SUNDAY)
        {
            start = start.minusDays(1);
        }


        return this.historyRepo.currentDaySticks(symbol, start)
                .map(mapper::toCandleStickDto)
                .doOnNext(entities::add)
                .doOnComplete(() -> {
                    Duration duration = getTTl(now, 5);
                    log.info("Sticks for the day cached till {}", now.plus(duration));
                    cache.put(key, entities, duration.getSeconds(), TimeUnit.SECONDS);
                });
    }

    private Duration getTTl(LocalDateTime now, int minutes)
    {
        /* *
        *  If minutes > 120 then the value has to be >= 3days, meaning you can cache
        *  this data till next day 9:00 and there won't be any change for the data anymore today.
        * */
        if(minutes > 120) return getTTLForNextDay(now);
        boolean isMarketClosed = now.isAfter(LocalDateTime.of(now.getYear(),
                                             now.getMonth(),
                                             now.getDayOfMonth(),
                                             15, 30));
        return isMarketClosed ? getTTLForNextDay(now) : getDurationForMarketOpen(now, minutes);
    }


    private Duration getDurationForMarketOpen(LocalDateTime now, int minute) {
        int currentMinute = now.getMinute() % minute;
        int secondsLeft = ((minute - currentMinute) * 60) - now.getSecond();
        return Duration.ofSeconds(secondsLeft);
    }


    private Duration getTTLForNextDay(LocalDateTime now) {
        LocalDateTime marketOpenNextDay = LocalDateTime.of(now.plusDays(1).toLocalDate(), LocalTime.of(9, 0, 0));
        return Duration.between(now, marketOpenNextDay);
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
                .doOnNext(entities::add)
                .doOnComplete(() -> {
                    int minutes = fetchMinutesByTimeSpace(timeSpace);
                    Duration ttl = getTTl(LocalDateTime.now(), minutes);
                    long seconds = ttl.getSeconds();
                    cache.put(key, entities, seconds , TimeUnit.SECONDS);
                })
                .doOnError(throwable -> System.out.println(throwable.getMessage()));
    }

    private String getKey(String timeSpace, String symbol) {
        switch (timeSpace){
            case CURRENT_DAY -> { return "1D:" + symbol; }
            case CURRENT_WEEK -> { return "1W:" + symbol; }
            case CURRENT_MONTH -> { return "1M:" + symbol; }
            default -> { return "TILL_NEXT_OPEN:" + symbol; }
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
}
