package org.bainsight.data.Persistence.Repository;

import com.redis.om.spring.repository.RedisDocumentRepository;
import org.bainsight.data.Model.Entity.CandleStick;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CandleStickRepo extends RedisDocumentRepository<CandleStick, String> {

    Optional<CandleStick> findCandleStickBySymbol(String symbol);

}
