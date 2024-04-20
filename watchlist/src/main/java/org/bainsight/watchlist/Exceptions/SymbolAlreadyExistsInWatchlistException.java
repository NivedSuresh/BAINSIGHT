package org.bainsight.watchlist.Exceptions;

import org.bainsight.watchlist.Payload.AddToWatchlist;
import org.exchange.library.Advice.Error;
import org.exchange.library.Exception.GlobalException;
import org.springframework.http.HttpStatus;

public class SymbolAlreadyExistsInWatchlistException extends GlobalException {
    public SymbolAlreadyExistsInWatchlistException(AddToWatchlist request) {
        super(
                request.symbol().concat(" already exists in ").concat(request.watchlistName()),
                HttpStatus.BAD_REQUEST,
                Error.DUPLICATE_SYMBOL_ADDITION
        );
    }
}
