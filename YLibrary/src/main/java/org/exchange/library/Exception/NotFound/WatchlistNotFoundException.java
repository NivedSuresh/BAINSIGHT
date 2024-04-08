package org.exchange.library.Exception.NotFound;

import org.exchange.library.Advice.Error;
import org.exchange.library.Exception.GlobalException;
import org.springframework.http.HttpStatus;

public class WatchlistNotFoundException extends GlobalException {
    public WatchlistNotFoundException(){
        super("WatchList not found!", HttpStatus.EXPECTATION_FAILED, Error.ENTITY_NOT_FOUND);
    }
}
