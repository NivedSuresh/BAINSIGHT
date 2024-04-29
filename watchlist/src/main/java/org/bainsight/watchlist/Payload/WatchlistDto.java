package org.bainsight.watchlist.Payload;

import lombok.Builder;
import org.bainsight.watchlist.CandleStick.Entity.CandleStick;
import org.exchange.library.Dto.Utils.BainsightPage;

import java.util.List;


@Builder
public record WatchlistDto (
    long watchlistId,
    String watchlistName,
    List<CandleStick> sticks,
    BainsightPage bainsightPage
){}
