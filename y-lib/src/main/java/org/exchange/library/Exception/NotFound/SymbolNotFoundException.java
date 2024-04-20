package org.exchange.library.Exception.NotFound;

import org.exchange.library.Advice.Error;
import org.exchange.library.Exception.GlobalException;
import org.springframework.http.HttpStatus;

public class SymbolNotFoundException extends GlobalException {

    public SymbolNotFoundException(String entity){
        super("Symbol not found in ".concat(entity), HttpStatus.NOT_FOUND, Error.SYMBOL_NOT_FOUND);
    }

    public SymbolNotFoundException() {
        super("Symbol not found", HttpStatus.NOT_FOUND, Error.SYMBOL_NOT_FOUND);
    }
}
