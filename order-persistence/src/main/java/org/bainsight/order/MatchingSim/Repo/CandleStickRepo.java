package org.bainsight.order.MatchingSim.Repo;



import org.bainsight.order.MatchingSim.Entity.CandleStick;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CandleStickRepo extends CrudRepository<CandleStick, String> { }
