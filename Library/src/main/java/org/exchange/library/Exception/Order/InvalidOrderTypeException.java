package org.exchange.library.Exception.Order;

import org.exchange.library.Advice.Error;
import org.exchange.library.Exception.GlobalException;
import org.springframework.http.HttpStatus;

public class InvalidOrderTypeException extends GlobalException {
    public InvalidOrderTypeException(String message, HttpStatus status, String errorCode) {
        super(message, status, errorCode);
    }

    public InvalidOrderTypeException() {
        super("Invalid order type provided!", HttpStatus.BAD_REQUEST, Error.INVALID_ORDER_TYPE);
    }
}
