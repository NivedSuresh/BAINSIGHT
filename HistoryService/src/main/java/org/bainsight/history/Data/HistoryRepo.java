package org.bainsight.history.Data;

import org.bainsight.history.Models.Entity.CandleStickEntity;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.List;


@Repository
interface HistoryRepo extends ReactiveCassandraRepository<CandleStickEntity, Long> {


}
