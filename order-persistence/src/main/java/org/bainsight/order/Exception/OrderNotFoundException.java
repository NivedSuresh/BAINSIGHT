package org.bainsight.order.Exception;

import org.exchange.library.Advice.Error;
import org.exchange.library.Exception.GlobalException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class OrderNotFoundException extends GlobalException {
    public OrderNotFoundException(UUID orderId) {
        super("Failed to find the order with the ID: ".concat(orderId.toString()), HttpStatus.NOT_FOUND, Error.NON_EXISTENT_ORDER);
    }

    public OrderNotFoundException() {
        super("Failed to find the order!", HttpStatus.NOT_FOUND, Error.NON_EXISTENT_ORDER);
    }
}
