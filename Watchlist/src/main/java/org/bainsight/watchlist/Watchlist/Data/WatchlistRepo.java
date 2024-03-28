package org.bainsight.watchlist.Watchlist.Data;

import org.bainsight.watchlist.Watchlist.Model.Entity.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
interface WatchlistRepo extends JpaRepository<Watchlist, Long> {
    Optional<Watchlist> findByUccAndPinned(UUID ucc, boolean isPinned);
}
