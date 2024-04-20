package org.bainsight.portfolio.Exceptions;

import org.exchange.library.Advice.Error;
import org.exchange.library.Exception.GlobalException;
import org.springframework.http.HttpStatus;

public class WalletNotFoundException extends GlobalException {
    public WalletNotFoundException() {
        super("Unable to find the wallet for the associated user!", HttpStatus.NOT_FOUND, Error.WALLET_NOT_FOUND);
    }
}
