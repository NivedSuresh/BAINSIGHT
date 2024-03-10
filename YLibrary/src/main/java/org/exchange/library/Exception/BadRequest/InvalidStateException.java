package org.exchange.library.Exception.BadRequest;

import org.exchange.library.Advice.Error;
import org.exchange.library.Exception.GlobalException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.EXPECTATION_FAILED)
public class InvalidStateException extends GlobalException {
    public InvalidStateException(String message, HttpStatus status, String errorCode) {
        super(message, status, errorCode);
    }

    public InvalidStateException(String message, String errorCode) {
        super(message, HttpStatus.EXPECTATION_FAILED, errorCode);
    }

    public InvalidStateException() {
        super("An unknown error occurred!", HttpStatus.EXPECTATION_FAILED, Error.UN_IDENTIFIED_EXCEPTION);
    }
}
