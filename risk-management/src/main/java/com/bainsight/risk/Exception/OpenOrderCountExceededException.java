package com.bainsight.risk.Exception;

import org.exchange.library.Advice.Error;
import org.exchange.library.Exception.GlobalException;
import org.springframework.http.HttpStatus;

public class OpenOrderCountExceededException extends GlobalException {
    public OpenOrderCountExceededException() {
        super(
                "Order cannot be proceeded as user has too many open orders pending",
                HttpStatus.BAD_REQUEST, Error.OPEN_ORDER_COUNT_LIMIT_EXCEEDED
        );
    }
}
