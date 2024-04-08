package org.bainsight.watchlist.Payload;

import jakarta.validation.constraints.NotBlank;

public record AddToWatchlist(
        @NotBlank(message = "Provide a valid name")
        String watchlistName,

        @NotBlank(message = "Invalid ticker symbol")
        String symbol
) {}
