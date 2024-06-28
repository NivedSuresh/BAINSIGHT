package org.bainsight.order.Domain;

import org.bainsight.order.Model.Entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface MatchRepo extends JpaRepository<Match, Long>, PagingAndSortingRepository<Match, Long> {
}
