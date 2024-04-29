package org.exchange.library.Exception.BadRequest;

import org.exchange.library.Advice.Error;
import org.exchange.library.Exception.GlobalException;
import org.springframework.http.HttpStatus;

public class MarketClosedException extends GlobalException {
    public MarketClosedException() {
        super("Order are accepted only if placed between 9am and 3:30pm.", HttpStatus.NOT_ACCEPTABLE, Error.MARKET_CLOSED);
    }
}
