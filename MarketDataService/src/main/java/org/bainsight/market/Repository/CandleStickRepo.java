package org.bainsight.market.Repository;

import org.bainsight.market.Model.Dto.CandleStick;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CandleStickRepo extends CrudRepository<CandleStick, String> {
    Optional<CandleStick> findBySymbol(String s);
    Optional<List<CandleStick>> searchCandleStickBySymbol(String symbol);
}
