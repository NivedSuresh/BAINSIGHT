package org.bainsight.watchlist.Mapper;

import org.bainsight.watchlist.CandleStick.Entity.CandleStick;
import org.bainsight.watchlist.Payload.WatchlistDto;
import org.bainsight.watchlist.Watchlist.Model.Watchlist;
import org.exchange.library.Dto.Utils.BainsightPage;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Mapper {

    public WatchlistDto toWatchlistDto(Watchlist watchlist, List<CandleStick> candleSticks, BainsightPage bainsightPage){
        return  WatchlistDto.builder()
                .watchlistId(watchlist.getWatchlistId())
                .watchlistName(watchlist.getWatchlistName())
                .sticks(candleSticks)
                .bainsightPage(bainsightPage)
                .pinned(watchlist.isPinned())
                .build();
    }

    public BainsightPage getPageable(Short page, int count, int size) {
        boolean next = page * count < size;
        boolean prev = page > 1;
        return new BainsightPage(page, next, prev);
    }

}
