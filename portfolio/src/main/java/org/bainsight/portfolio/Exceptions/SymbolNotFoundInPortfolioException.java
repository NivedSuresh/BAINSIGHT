package org.bainsight.portfolio.Exceptions;

import org.exchange.library.Advice.Error;
import org.exchange.library.Exception.GlobalException;
import org.springframework.http.HttpStatus;

public class SymbolNotFoundInPortfolioException extends GlobalException {
    public SymbolNotFoundInPortfolioException() {
        super("Unable to sell the symbol as it's not present in the user's portfolio.", HttpStatus.NOT_FOUND, Error.SYMBOL_NOT_FOUND);
    }
}
