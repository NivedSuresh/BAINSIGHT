package com.bainsight.risk.Exception;

import org.exchange.library.Advice.Error;
import org.exchange.library.Exception.GlobalException;
import org.springframework.http.HttpStatus;

public class FailedToAcquireLockException extends GlobalException {
    public FailedToAcquireLockException() {
        super("Try again after sometime!", HttpStatus.BAD_REQUEST, Error.FAILED_TO_ACQUIRE_LOCK);
    }

    public FailedToAcquireLockException(String message) {
        super(message, HttpStatus.BAD_REQUEST, Error.FAILED_TO_ACQUIRE_LOCK);
    }
}
