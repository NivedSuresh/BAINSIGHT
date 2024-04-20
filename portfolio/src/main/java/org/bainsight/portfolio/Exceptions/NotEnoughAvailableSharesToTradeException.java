package org.bainsight.portfolio.Exceptions;

import org.exchange.library.Advice.Error;
import org.exchange.library.Exception.GlobalException;
import org.springframework.http.HttpStatus;

public class NotEnoughAvailableSharesToTradeException extends GlobalException {
    public NotEnoughAvailableSharesToTradeException() {
        super("User doesn't have enough available shares to trade.", HttpStatus.BAD_REQUEST, Error.NOT_ENOUGH_SECURITY_TO_SELL);
    }
}
