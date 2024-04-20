package org.exchange.library.Mapper;

import org.exchange.library.Exception.Authentication.BadBindException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindingResult;

public class ValidationErrorMapper {
    public static BadBindException fetchFirstError(BindingResult bindingResult) {

        String message = bindingResult.getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .findFirst().orElse("Validation error occurred");

        return new BadBindException(message);
    }


}
