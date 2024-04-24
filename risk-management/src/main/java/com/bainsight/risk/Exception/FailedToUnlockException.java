package com.bainsight.risk.Exception;

import org.exchange.library.Advice.Error;
import org.exchange.library.Exception.GlobalException;
import org.springframework.http.HttpStatus;

public class FailedToUnlockException extends GlobalException {
    public FailedToUnlockException() {
        super("An unexpected error occurred, please try again later!", HttpStatus.INTERNAL_SERVER_ERROR, Error.FAILED_TO_RELEASE_LOCK);
    }
}
