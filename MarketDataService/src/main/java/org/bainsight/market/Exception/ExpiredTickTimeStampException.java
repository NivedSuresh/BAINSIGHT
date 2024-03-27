package org.bainsight.market.Exception;

import org.exchange.library.Advice.Error;

public class ExpiredTickTimeStampException extends RuntimeException {

    private final String errorCode;
    public ExpiredTickTimeStampException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public static ExpiredTickTimeStampException trigger() throws ExpiredTickTimeStampException{
        throw new ExpiredTickTimeStampException("Stick expired", Error.CANDLE_STICK_EXPIRED);    }
}
