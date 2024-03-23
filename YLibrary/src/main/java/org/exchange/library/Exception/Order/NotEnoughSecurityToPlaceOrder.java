package org.exchange.library.Exception.Order;

import org.exchange.library.Advice.Error;
import org.exchange.library.Exception.GlobalException;
import org.springframework.http.HttpStatus;

public class NotEnoughSecurityToPlaceOrder extends GlobalException {
    public NotEnoughSecurityToPlaceOrder(String message, String errorCode) {
        super(message, HttpStatus.BAD_REQUEST, errorCode);
    }

    public NotEnoughSecurityToPlaceOrder() {
        super(
                "Client doesn't have enough holdings to place this Order",
                HttpStatus.BAD_REQUEST,
                Error.NOT_ENOUGH_SECURITY_TO_SELL
        );
    }
}
