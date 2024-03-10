package org.exchange.library.Exception.Order;

import org.exchange.library.Advice.Error;
import org.exchange.library.Exception.GlobalException;
import org.springframework.http.HttpStatus;

public class InvalidTransactionTypeException extends GlobalException {
    public InvalidTransactionTypeException(String message, HttpStatus status, String errorCode) {
        super(message, status, errorCode);
    }

    public InvalidTransactionTypeException() {
        super(
                "",
                HttpStatus.BAD_REQUEST,
                Error.INVALID_ORDER_CATEGORY_FOR_REQUEST
        );
    }
}
