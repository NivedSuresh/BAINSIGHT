package org.bainsight.watchlist.Watchlist.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bainsight.watchlist.Payload.WatchlistDto;

import java.util.List;

@Data
@AllArgsConstructor
public class WatchlistMeta {
   private WatchlistDto watchlistDto;
   private List<String> tags;
}
