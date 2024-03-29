package org.exchange.library.Exception.IO;


import org.exchange.library.Advice.Error;
import org.exchange.library.Exception.GlobalException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ConnectionFailureException extends GlobalException {
    public ConnectionFailureException(String message, HttpStatus status, String errorCode) {
        super(message, status, errorCode);
    }


    public ConnectionFailureException() {
        super(
                "Unable to perform this operation at the moment, please try again later!",
                HttpStatus.INTERNAL_SERVER_ERROR,
                Error.DATABASE_INTERACTION_FAILED
        );
    }
}
