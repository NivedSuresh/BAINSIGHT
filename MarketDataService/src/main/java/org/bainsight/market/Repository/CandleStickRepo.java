package org.bainsight.market.Repository;

import com.redis.om.spring.annotations.Query;
import com.redis.om.spring.repository.RedisDocumentRepository;
import org.bainsight.market.Model.Entity.CandleStick;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CandleStickRepo extends RedisDocumentRepository<CandleStick, String> {
//    Optional<CandleStick> findBySymbol(String s);
//    Optional<List<CandleStick>> searchCandleStickBySymbol(String symbol);
//    @Query("SELECT c FROM CandleStick c WHERE c.symbol NOT IN :symbols")
//    Optional<List<CandleStick>> findCandleSticksBySymbolNotIn(List<String> symbols);


    Optional<CandleStick> findCandleStickBySymbol(String symbol);

}
