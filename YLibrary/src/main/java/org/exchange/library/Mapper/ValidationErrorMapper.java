package org.exchange.library.Mapper;

import org.exchange.library.Exception.Authentication.InvalidCredentialsException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.support.WebExchangeBindException;

public class ValidationErrorMapper {
    public static InvalidCredentialsException fetchFirstError(WebExchangeBindException webBindException) {
        BindingResult bindingResult = webBindException.getBindingResult();
        String message = bindingResult.getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .findFirst().orElse("Validation error occurred");
        return new InvalidCredentialsException(message);
    }
}
