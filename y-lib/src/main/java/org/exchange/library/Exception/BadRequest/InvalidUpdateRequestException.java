package org.exchange.library.Exception.BadRequest;

import org.exchange.library.Advice.Error;
import org.exchange.library.Exception.GlobalException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidUpdateRequestException extends GlobalException {

    public InvalidUpdateRequestException(String entity, String identifier) {
        super("Invalid update request, no information available for ".concat(entity).concat(" with the identifier ").concat(identifier),
                HttpStatus.BAD_REQUEST,
                Error.ENTITY_NOT_FOUND
        );
    }

}
