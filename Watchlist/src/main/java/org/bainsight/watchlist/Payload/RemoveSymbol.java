package org.bainsight.watchlist.Payload;

public record RemoveSymbol(
        Long watchlistId,
        String symbol

) { }
