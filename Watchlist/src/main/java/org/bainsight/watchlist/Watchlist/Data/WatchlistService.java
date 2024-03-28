package org.bainsight.watchlist.Watchlist.Data;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bainsight.watchlist.Watchlist.Model.Entity.Watchlist;
import org.exchange.library.Exception.IO.ServiceUnavailableException;
import org.exchange.library.Exception.NotFound.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WatchlistService {

    private final WatchlistRepo watchlistRepo;
    public Set<String> findPinnedWatchListByUCC(String ucc) {
        try{
            return this.watchlistRepo.findByUccAndPinned(UUID.fromString(ucc), true)
                    .map(Watchlist::getSymbols)
                    .orElseThrow(() -> EntityNotFoundException.triggerDefault("Watchlist"));
        }
        catch (Exception e){
            if(e instanceof EntityNotFoundException) throw e;
            log.error(e.getMessage());
            throw new ServiceUnavailableException();
        }
    }
}
