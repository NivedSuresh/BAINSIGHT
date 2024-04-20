package org.bainsight.watchlist.Watchlist.Data;

import org.bainsight.watchlist.Payload.WatchlistDto;
import org.bainsight.watchlist.Watchlist.Model.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
interface WatchlistRepo extends JpaRepository<Watchlist, Long> {
    Optional<Watchlist> findByUccAndPinned(UUID ucc, boolean isPinned);

    Optional<Watchlist> findByUccAndWatchlistName(UUID ucc, String watchlistName);

    boolean existsByUcc(UUID ucc);

    @Modifying
    @Query("UPDATE Watchlist w set w.pinned = false where w.ucc = :ucc")
    void unpinCurrentlyPinned(UUID ucc);

    @Modifying
    @Query(nativeQuery = true, value = "UPDATE watchlist w set w.is_pinned = true where w.ucc = :ucc and w.watchlist_id = :id")
    void pinWatchlist(UUID ucc, Long id);

    boolean existsByUccAndWatchlistId(UUID ucc, Long id);

    Optional<Watchlist> findByWatchlistIdAndUcc(Long id, UUID ucc);

    @Modifying
    @Query(nativeQuery = true, value = "DELETE from watchlist_symbols w where w.watchlist_id = :id and w.symbol = :symbol")
    int removeSymbolFromWatchlist(Long id, String symbol);


    @Query("SELECT w.watchlistName from Watchlist as w where w.ucc = :ucc")
    Optional<List<String>> fetchAllWatchlistNamesByUCC(UUID ucc);


}
