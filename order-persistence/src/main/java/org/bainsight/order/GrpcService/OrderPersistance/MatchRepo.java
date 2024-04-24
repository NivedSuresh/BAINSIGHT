package org.bainsight.order.GrpcService.OrderPersistance;

import org.bainsight.order.Model.Entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchRepo extends JpaRepository<Match, Long> {
}
