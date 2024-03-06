package org.exchange.library.Exception.Order;

import org.exchange.library.Advice.Error;
import org.exchange.library.Exception.GlobalException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidOrderRequestException extends GlobalException {
    public InvalidOrderRequestException(String message, String errorCode) {
        super(message, HttpStatus.BAD_REQUEST, errorCode);
    }

    public InvalidOrderRequestException() {
        super("Failed to map the customer with the provided symbol", HttpStatus.BAD_REQUEST, Error.INVALID_ORDER_REQUEST);
    }
}
