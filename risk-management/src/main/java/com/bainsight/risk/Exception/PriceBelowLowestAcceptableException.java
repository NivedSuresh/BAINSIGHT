package com.bainsight.risk.Exception;

import org.exchange.library.Advice.Error;
import org.exchange.library.Exception.GlobalException;
import org.springframework.http.HttpStatus;

public class PriceBelowLowestAcceptableException extends GlobalException {
    public PriceBelowLowestAcceptableException(double lowestAcceptable, String symbol) {
        super(
                "The Order cannot be accepted as the lowest acceptable price for the symbol ".concat(symbol).concat(" is ").concat(String.valueOf(lowestAcceptable)),
                HttpStatus.BAD_REQUEST, Error.PRICE_BELOW_ACCEPTABLE
        );
    }
}
