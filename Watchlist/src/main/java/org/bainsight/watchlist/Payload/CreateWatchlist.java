package org.bainsight.watchlist.Payload;

import jakarta.validation.constraints.NotBlank;

public record CreateWatchlist(
        @NotBlank (message = "A name should be provided")
        String watchlistName
) { }
