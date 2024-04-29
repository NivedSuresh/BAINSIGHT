package org.bainsight.watchlist.Watchlist.Data;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bainsight.watchlist.Exceptions.SymbolAlreadyExistsInWatchlistException;
import org.bainsight.watchlist.Payload.AddToWatchlist;
import org.bainsight.watchlist.Payload.RemoveSymbol;
import org.bainsight.watchlist.Watchlist.Model.Watchlist;
import org.exchange.library.Exception.BadRequest.EntityAlreadyExistsException;
import org.exchange.library.Exception.BadRequest.InvalidUpdateRequestException;
import org.exchange.library.Exception.NotFound.SymbolNotFoundException;
import org.exchange.library.Exception.NotFound.WatchlistNotFoundException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WatchlistService {

    private final WatchlistRepo watchlistRepo;


    public Watchlist findPinnedWatchListByUCC(final String ucc)
    {
       return this.watchlistRepo
               .findByUccAndPinned(UUID.fromString(ucc), true)
               .orElseThrow(WatchlistNotFoundException::new);

    }


    /**
     * Will add the symbol to the watchlist with the corresponding name.
     * If watchlist doesn't exist with the ucc:watchlist_name pair, then a new list
     * is created and the symbol is added.
     * */
    public Watchlist computeWatchlistIfAbsent(final String ucc, final AddToWatchlist request)
    {

        Watchlist watchlist;
        UUID uniqueClientId = UUID.fromString(ucc);
        Optional<Watchlist> optionalWatchlist = this.watchlistRepo.findByUccAndWatchlistName(uniqueClientId, request.watchlistName());

        if(optionalWatchlist.isPresent())
        {
            watchlist = addSymbolElseThrow(optionalWatchlist.get(), request);
        }
        else
        {
            List<String> symbols = new ArrayList<>();
            symbols.add(request.symbol());

            watchlist = Watchlist.builder()
                    .ucc(uniqueClientId)
                    .watchlistName(request.watchlistName())
                    .symbols(symbols)
                        /* IF NO OTHER WATCHLIST EXISTS FOR USER THEN DEFAULT PIN IT */
                    .pinned(shouldPin(optionalWatchlist, uniqueClientId))
                    .build();

        }

        return this.saveWatchlist(watchlist);
    }

    private boolean shouldPin(Optional<Watchlist> optionalWatchlist, UUID uniqueClientId) {
        /* IF OPTIONAL WATCHLIST IS PRESENT THEN THE PINNING PART WAS ALREADY TAKEN CARE */
        if(optionalWatchlist.isPresent()) return false;
        return !this.watchlistRepo.existsByUcc(uniqueClientId);
    }

    private Watchlist addSymbolElseThrow(Watchlist watchlist, AddToWatchlist request) {
        if(watchlist.getSymbols().contains(request.symbol())){
            throw new SymbolAlreadyExistsInWatchlistException(request);
        }
        watchlist.getSymbols().add(request.symbol());
        return watchlist;
    }


    @Transactional
    public void updateCurrentlyPinned(String ucc, Long id) {
        UUID uniqueIdentifier = UUID.fromString(ucc);
        this.watchlistRepo.unpinCurrentlyPinned(uniqueIdentifier);
        try{
            this.watchlistRepo.pinWatchlist(uniqueIdentifier, id);
        }
        catch (DataAccessException e){
            throw new InvalidUpdateRequestException("Watchlist", String.valueOf(id));
        }
    }

    public Watchlist createNewWatchlist(String ucc, String watchlistName) {
        UUID uniqueClientId = UUID.fromString(ucc);
        Watchlist watchlist = Watchlist.builder()
                .watchlistName(watchlistName)
                .ucc(uniqueClientId)
                .pinned(!this.watchlistRepo.existsByUcc(uniqueClientId))
                .build();
        try { return this.saveWatchlist(watchlist); }
        catch (DataIntegrityViolationException e){
            throw new EntityAlreadyExistsException("Watchlist", watchlistName);
        }
    }

    public Watchlist findWatchlistByUccAndId(String ucc, Long id) {
        return this.watchlistRepo.findByWatchlistIdAndUcc(id, UUID.fromString(ucc))
                .orElseThrow(WatchlistNotFoundException::new);
    }

    public boolean watchlistExistsByUccAndId(String ucc, Long id, boolean elseThrow){
        boolean exists = this.watchlistRepo.existsByUccAndWatchlistId(UUID.fromString(ucc), id);
        if(!exists && elseThrow) throw new WatchlistNotFoundException();
        return exists;
    }

    @Transactional
    public void removeSymbolFromWatchlist(RemoveSymbol removeSymbol){

        int rowsEffected = this.watchlistRepo.removeSymbolFromWatchlist(removeSymbol.watchlistId(), removeSymbol.symbol());
        if(rowsEffected > 1){
            /* TODO: JOURNALING */
            log.error("More than one row effected while removing a symbol from Watchlist");
        }
        else if(rowsEffected == 0)
        {
            throw new SymbolNotFoundException("Watchlist");
        }
    }



    public Watchlist saveWatchlist(Watchlist watchlist) {
        return this.watchlistRepo.save(watchlist);
    }

    public List<String> fetchAllWatchlistNamesForUcc(String ucc) {
        return this.watchlistRepo.fetchAllWatchlistNamesByUCC(UUID.fromString(ucc))
                .orElseThrow(WatchlistNotFoundException::new);
    }

    public Watchlist findWatchlistByUccAndName(String ucc, String watchlistName) {
        return this.watchlistRepo.findByUccAndWatchlistName(UUID.fromString(ucc), watchlistName)
                .orElseThrow(WatchlistNotFoundException::new);
    }

}
