package org.bainsight.history.Data;

import org.bainsight.history.Models.Entity.CandleStickEntity;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;


@Repository
interface HistoryRepo extends ReactiveCassandraRepository<CandleStickEntity, Long> {

    @Query("SELECT * FROM candle_sticks WHERE timestamp > :start AND symbol = :symbol")
    Flux<CandleStickEntity> currentDaySticks(String symbol, LocalDateTime start);


    @Query("SELECT * FROM candle_sticks WHERE timestamp = :time AND symbol = :symbol")
    Mono<CandleStickEntity> findByTimeStamp(LocalDateTime time, String symbol);


}
