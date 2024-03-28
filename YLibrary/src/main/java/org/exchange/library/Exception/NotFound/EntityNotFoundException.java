package org.exchange.library.Exception.NotFound;

import lombok.Getter;
import org.exchange.library.Advice.Error;
import org.exchange.library.Exception.GlobalException;
import org.springframework.http.HttpStatus;

public class EntityNotFoundException extends GlobalException {
    public EntityNotFoundException(String message, HttpStatus status, String errorCode) {
        super(message, status, errorCode);
    }

    private EntityNotFoundException(String entityName){
        super("No ".concat(entityName).concat(" found for the user"), HttpStatus.NO_CONTENT, Error.ENTITY_NOT_FOUND);
    }

    public static EntityNotFoundException triggerDefault(String entityName){
        throw new EntityNotFoundException(entityName);
    }
}
