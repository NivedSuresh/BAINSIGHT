package com.bainsight.risk.Exception;

import org.exchange.library.Advice.Error;
import org.exchange.library.Exception.GlobalException;
import org.springframework.http.HttpStatus;

public class SpendLimitPerDayExceededException extends GlobalException {
    public SpendLimitPerDayExceededException(double exceededAmount, Double spendable) {
        super(
                "User needs to reduce the price by ".concat(String.valueOf(exceededAmount)).concat(" before making the purchase. Maximum spendable amount per day is ").concat(String.valueOf(spendable)),
                HttpStatus.BAD_REQUEST, Error.SPEND_LIMIT_EXCEEDED
        );
    }
}
