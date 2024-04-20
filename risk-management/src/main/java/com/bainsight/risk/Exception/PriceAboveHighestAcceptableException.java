package com.bainsight.risk.Exception;

import org.exchange.library.Advice.Error;
import org.exchange.library.Exception.GlobalException;
import org.springframework.http.HttpStatus;

public class PriceAboveHighestAcceptableException extends GlobalException {
    public PriceAboveHighestAcceptableException(double highestAcceptable, String symbol) {
        super(
                "The Order cannot be accepted as the highest acceptable price for the symbol ".concat(symbol).concat(" is ").concat(String.valueOf(highestAcceptable)),
                HttpStatus.BAD_REQUEST, Error.PRICE_BELOW_ACCEPTABLE
        );
    }
}
