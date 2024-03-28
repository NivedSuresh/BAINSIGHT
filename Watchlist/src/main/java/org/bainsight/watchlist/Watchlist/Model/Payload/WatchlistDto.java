package org.bainsight.watchlist.Watchlist.Model.Payload;

import org.exchange.library.Dto.MarketRelated.CandleStick;

import java.util.List;

public class WatchlistDto {
    private Long watchlistId;
    private String watchlistName;
    private List<CandleStick> sticks;
}
