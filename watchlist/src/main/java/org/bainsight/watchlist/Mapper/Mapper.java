package org.bainsight.watchlist.Mapper;

import org.bainsight.watchlist.CandleStick.Entity.CandleStick;
import org.bainsight.watchlist.Payload.WatchlistDto;
import org.bainsight.watchlist.Watchlist.Model.Watchlist;
import org.exchange.library.Dto.Utils.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Mapper {

    public WatchlistDto toWatchlistDto(Watchlist watchlist, List<CandleStick> candleSticks, Page page){
        return  WatchlistDto.builder()
                .watchlistId(watchlist.getWatchlistId())
                .watchlistName(watchlist.getWatchlistName())
                .sticks(candleSticks)
                .page(page)
                .build();
    }

    public Page getPageable(Short page, int count, int size) {
        boolean next = page * count < size;
        boolean prev = page > 1;
        return new Page(page, next, prev);
    }
}
