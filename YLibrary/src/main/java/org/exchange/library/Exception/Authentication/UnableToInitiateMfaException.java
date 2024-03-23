package org.exchange.library.Exception.Authentication;

import org.exchange.library.Advice.Error;
import org.exchange.library.Exception.GlobalException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class UnableToInitiateMfaException extends GlobalException {
    public UnableToInitiateMfaException(String message, HttpStatus status, String errorCode) {
        super(message, status, errorCode);
    }

    public UnableToInitiateMfaException() {
        super(
                "Unable to initiate Multi Factor Authentication. If issue persist's please contact",
                HttpStatus.INTERNAL_SERVER_ERROR,
                Error.MFA_INITIAL_FAILURE
        );
    }
}
