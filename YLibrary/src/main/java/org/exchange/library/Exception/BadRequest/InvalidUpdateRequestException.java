package org.exchange.library.Exception.BadRequest;

import org.exchange.library.Exception.GlobalException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidUpdateRequestException extends GlobalException {
    public InvalidUpdateRequestException(String message, HttpStatus status, String errorCode) {
        super(message, status, errorCode);
    }

    public InvalidUpdateRequestException(String errorCode) {
        super(
                "BadRequest update request, no information available for the provided details!",
                HttpStatus.BAD_REQUEST,
                errorCode
        );
    }
}
