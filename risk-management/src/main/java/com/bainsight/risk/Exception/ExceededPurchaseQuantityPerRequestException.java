package com.bainsight.risk.Exception;

import org.exchange.library.Advice.Error;
import org.exchange.library.Exception.GlobalException;
import org.springframework.http.HttpStatus;

public class ExceededPurchaseQuantityPerRequestException extends GlobalException {
    public ExceededPurchaseQuantityPerRequestException(Long quantity) {
        super("The maximum allowed order quantity per purchase is ".concat(String.valueOf(quantity)),
                HttpStatus.BAD_REQUEST, Error.PURCHASE_QUANTITY_EXCEEDED);
    }
}
