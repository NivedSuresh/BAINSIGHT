package org.bainsight.processing.Exception;

import org.exchange.library.Advice.Error;
import org.exchange.library.Exception.GlobalException;
import org.springframework.http.HttpStatus;

public class FailedToCompleteOrderException extends GlobalException {
    public FailedToCompleteOrderException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR, Error.FAILED_TO_COMPLETE_ORDER);
    }
}
