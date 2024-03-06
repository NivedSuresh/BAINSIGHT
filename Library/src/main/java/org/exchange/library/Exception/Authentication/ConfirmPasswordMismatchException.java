package org.exchange.library.Exception.Authentication;

import org.exchange.library.Exception.GlobalException;
import org.springframework.http.HttpStatus;

public class ConfirmPasswordMismatchException extends GlobalException {
    public ConfirmPasswordMismatchException(String errorCode) {
        super("The given password doesn't match with the confirm password field", HttpStatus.BAD_REQUEST, errorCode);
    }

}
