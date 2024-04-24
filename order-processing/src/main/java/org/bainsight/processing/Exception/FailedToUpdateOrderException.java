package org.bainsight.processing.Exception;

import org.exchange.library.Advice.Error;
import org.exchange.library.Exception.GlobalException;
import org.springframework.http.HttpStatus;

public class FailedToUpdateOrderException extends GlobalException {
    public FailedToUpdateOrderException() {
        super("Failed to update order, try again later!", HttpStatus.INTERNAL_SERVER_ERROR, Error.FAILED_TO_UPDATE_ORDER);
    }

    public FailedToUpdateOrderException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR, Error.FAILED_TO_UPDATE_ORDER);
    }
}
