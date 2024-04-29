package org.bainsight.order.Exception;

import org.exchange.library.Advice.Error;
import org.exchange.library.Exception.GlobalException;
import org.springframework.http.HttpStatus;

public class MatchNotFoundException extends GlobalException {
    public MatchNotFoundException(long matchId) {
        super("Unable to find any match with the id ".concat(String.valueOf(matchId)), HttpStatus.NOT_FOUND, Error.MATCH_NOT_FOUND);
    }
}
