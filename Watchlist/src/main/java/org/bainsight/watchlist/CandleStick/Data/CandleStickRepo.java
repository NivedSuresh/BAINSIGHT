package org.bainsight.watchlist.CandleStick.Data;

import com.redis.om.spring.repository.RedisDocumentRepository;
import org.bainsight.watchlist.CandleStick.Entity.CandleStick;
import org.springframework.stereotype.Repository;

@Repository
public interface CandleStickRepo extends RedisDocumentRepository<CandleStick, String> { }
