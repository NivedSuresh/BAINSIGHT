package org.exchange.library.Exception.BadRequest;

import org.exchange.library.Advice.Error;
import org.exchange.library.Exception.GlobalException;
import org.springframework.http.HttpStatus;

public class NotImplementedException extends GlobalException {
    public NotImplementedException(String message, HttpStatus status, String errorCode) {
        super(message, status, errorCode);
    }

    public NotImplementedException() {
        super("This service is not yet implemented!", HttpStatus.BAD_REQUEST, Error.SERVICE_NOT_IMPLEMENTED);
    }
}
