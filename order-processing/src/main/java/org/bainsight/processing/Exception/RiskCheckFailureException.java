package org.bainsight.processing.Exception;

import org.exchange.library.Advice.Error;
import org.exchange.library.Exception.GlobalException;
import org.springframework.http.HttpStatus;

public class RiskCheckFailureException extends GlobalException {
    public RiskCheckFailureException(String message) {
        super(message, HttpStatus.BAD_REQUEST, Error.RISK_CHECK_FAILED);
    }
}
