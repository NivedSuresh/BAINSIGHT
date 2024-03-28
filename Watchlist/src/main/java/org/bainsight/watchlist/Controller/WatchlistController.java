package org.bainsight.watchlist.Controller;

import lombok.RequiredArgsConstructor;
import org.bainsight.watchlist.CandleStick.Service.CandleStickService;
import org.bainsight.watchlist.Watchlist.Data.WatchlistService;
import org.bainsight.watchlist.Watchlist.Model.Payload.WatchlistDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@RestController
public class WatchlistController {

    private final WatchlistService watchlistService;
    private final CandleStickService candleStickService;

    @ResponseStatus(HttpStatus.OK)
    public List<WatchlistDto> fetchPinnedWatchlistForUser(@RequestHeader("x-auth-user-id") String ucc){
        Set<String> pinnedWatchListByUCC = this.watchlistService.findPinnedWatchListByUCC(ucc);

        if(pinnedWatchListByUCC.isEmpty()) return Collections.emptyList();

        return null;
    }

}
