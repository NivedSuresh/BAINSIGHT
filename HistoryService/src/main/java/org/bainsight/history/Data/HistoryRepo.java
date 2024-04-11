package org.bainsight.history.Data;

import org.bainsight.history.Models.Entity.CandleStickEntity;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;
import reactor.core.CorePublisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;


@Repository
interface HistoryRepo extends ReactiveCassandraRepository<CandleStickEntity, Long> {

    @Query("SELECT * FROM candle_sticks WHERE timestamp > :start AND symbol = :symbol")
    Flux<CandleStickEntity> currentDaySticks(String symbol, LocalDateTime start);


    @Query("SELECT * FROM candle_sticks WHERE timestamp = :time AND symbol = :symbol")
    Mono<CandleStickEntity> findByTimeStamp(LocalDateTime time, String symbol);

    @AllowFiltering
    @Query("SELECT symbol, timestamp, open, close, low, high, volume, MAX(change) AS change FROM candle_sticks WHERE timestamp = :latest")
    Mono<CandleStickEntity> findGainersForTheDay(LocalDateTime latest);


    @AllowFiltering
    @Query("SELECT symbol, timestamp, open, close, low, high, volume, MIN(change) AS change FROM candle_sticks WHERE timestamp = :latest")
    Mono<CandleStickEntity> findLosersForTheDay(LocalDateTime latest);

    @AllowFiltering
    @Query("select * from candle_sticks where change = :change and timestamp = :timestamp limit 1 ALLOW FILTERING")
    Mono<CandleStickEntity> findCandleStickEntityByChangeAndTimeStamp(double change, LocalDateTime timestamp);

    @AllowFiltering
    @Query("select max(change) as change from candle_sticks where timestamp = :timestamp")
    Mono<CandleStickEntity> findMaxChangeByTimestamp(LocalDateTime timestamp);

    @AllowFiltering
    @Query("select min(change) as change from candle_sticks where timestamp = :timestamp")
    Mono<CandleStickEntity> findMinChangeByTimestamp(LocalDateTime timestamp);


    @Query("select max(high) as high from candle_sticks where symbol = :symbol and timestamp >= :start and timestamp <= :end ALLOW FILTERING")
    Mono<CandleStickEntity> findHighForSymbolBwTimestamp(LocalDateTime start, LocalDateTime end, String symbol);

    @Query("select min(low) as low from candle_sticks where symbol = :symbol and timestamp >= :start and timestamp <= :end ALLOW FILTERING")
    Mono<CandleStickEntity> findLowForSymbolBwTimestamp(LocalDateTime start, LocalDateTime end, String symbol);
}