package org.exchange.library.Exception.BadRequest;

import org.exchange.library.Advice.Error;
import org.exchange.library.Exception.GlobalException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EntityAlreadyExistsException extends GlobalException {
    public EntityAlreadyExistsException(String message, HttpStatus status, String errorCode) {
        super(message, status, errorCode);
    }


    /*
        what -> Watchlist, Broker etc
        name -> Username/email/specific-field provided as the identifier while signing up
    */
    public EntityAlreadyExistsException(String entity, String identifier) {
        super(entity.concat(" with the identifier ")
                        .concat(identifier).concat(" already exists"),
                HttpStatus.BAD_REQUEST,
                Error.ENTITY_ALREADY_EXISTS
        );
    }

}
