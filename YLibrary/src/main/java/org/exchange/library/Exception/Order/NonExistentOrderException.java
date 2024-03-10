package org.exchange.library.Exception.Order;

import org.exchange.library.Advice.Error;
import org.exchange.library.Exception.GlobalException;
import org.springframework.http.HttpStatus;

public class NonExistentOrderException extends GlobalException {
    public NonExistentOrderException(String message, HttpStatus status, String errorCode) {
        super(message, status, errorCode);
    }

    public NonExistentOrderException() {
        super("No update was made as order is non existent for the email", HttpStatus.BAD_REQUEST, Error.NON_EXISTENT_ORDER);
    }
}
