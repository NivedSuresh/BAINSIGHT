package org.exchange.library.Exception.Authentication;


import org.exchange.library.Advice.Error;
import org.exchange.library.Exception.GlobalException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadBindException extends GlobalException {
    public BadBindException(String message, HttpStatus status, String errorCode) {
        super(message, status, errorCode);
    }

    public BadBindException() {
        super(
                "Invalid credentials provided.",
                HttpStatus.BAD_REQUEST, Error.INVALID_CREDENTIALS
        );
    }

    public BadBindException(String message) {
        super(message, HttpStatus.BAD_REQUEST, Error.INVALID_CREDENTIALS);
    }
}
