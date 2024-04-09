package org.exchange.library.Exception.BadRequest;

import org.exchange.library.Advice.Error;
import org.exchange.library.Exception.GlobalException;
import org.springframework.http.HttpStatus;

public class InvalidTimeSpaceException extends GlobalException {
    public InvalidTimeSpaceException(){
        super("Invalid time space provided", HttpStatus.BAD_REQUEST, Error.INVALID_TIME_SPACE);
    }
}
