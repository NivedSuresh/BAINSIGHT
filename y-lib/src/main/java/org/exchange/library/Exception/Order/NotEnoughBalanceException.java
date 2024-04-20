package org.exchange.library.Exception.Order;

import org.exchange.library.Advice.Error;
import org.exchange.library.Exception.GlobalException;
import org.springframework.http.HttpStatus;

public class NotEnoughBalanceException extends GlobalException {
    public NotEnoughBalanceException(String message, HttpStatus status, String errorCode) {
        super(message, status, errorCode);
    }

    public NotEnoughBalanceException() {
        super(
                "The user doesn't have enough balance to proceed with the order",
                HttpStatus.BAD_REQUEST,
                Error.NOT_ENOUGH_BALANCE_TO_BID


        );
    }
}
