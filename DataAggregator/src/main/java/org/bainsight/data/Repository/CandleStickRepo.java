package org.bainsight.data.Repository;

import com.redis.om.spring.repository.RedisDocumentRepository;
import org.bainsight.data.Model.Entity.CandleStick;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CandleStickRepo extends RedisDocumentRepository<CandleStick, String> {
//    Optional<CandleStick> findBySymbol(String s);
//    Optional<List<CandleStick>> searchCandleStickBySymbol(String symbol);
//    @Query("SELECT c FROM CandleStick c WHERE c.symbol NOT IN :symbols")
//    Optional<List<CandleStick>> findCandleSticksBySymbolNotIn(List<String> symbols);


    Optional<CandleStick> findCandleStickBySymbol(String symbol);

}
