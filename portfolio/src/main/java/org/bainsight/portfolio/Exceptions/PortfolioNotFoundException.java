package org.bainsight.portfolio.Exceptions;

import org.exchange.library.Advice.Error;
import org.exchange.library.Exception.GlobalException;
import org.springframework.http.HttpStatus;

public class PortfolioNotFoundException extends GlobalException {
    public PortfolioNotFoundException() {
        super("", HttpStatus.NOT_FOUND, Error.PORTFOLIO_NOT_FOUND);
    }
}
