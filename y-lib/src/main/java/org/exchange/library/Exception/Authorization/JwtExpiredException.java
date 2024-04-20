package org.exchange.library.Exception.Authorization;

import org.exchange.library.Advice.Error;
import org.exchange.library.Exception.GlobalException;
import org.springframework.http.HttpStatus;

public class JwtExpiredException extends GlobalException {
    public JwtExpiredException(String message, HttpStatus status, String errorCode) {
        super(message, status, errorCode);
    }

    public JwtExpiredException(){
        super("Your login session has expired", HttpStatus.FORBIDDEN, Error.JWT_EXPIRED);
    }
}
