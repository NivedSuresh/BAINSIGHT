package org.exchange.library.Exception.Authorization;

import org.exchange.library.Advice.Error;
import org.exchange.library.Exception.GlobalException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class InvalidJwtException extends GlobalException {
    public InvalidJwtException(String message, HttpStatus status, String errorCode) {
        super(message, status, errorCode);
    }

    public InvalidJwtException(String errorCode) {
        super(
                "You're UnAuthorized to access this URL, please authenticate yourself and try again.",
                HttpStatus.FORBIDDEN,
                errorCode
        );
    }

    public InvalidJwtException() {
        super(
                "You're UnAuthorized to access this URL, please authenticate yourself and try again.",
                HttpStatus.FORBIDDEN,
                Error.INVALID_JWT
        );
    }
}
