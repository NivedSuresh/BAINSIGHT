package org.bainsight.history.Advice;

import org.exchange.library.Advice.Error;
import org.exchange.library.Advice.ErrorResponse;
import org.exchange.library.Exception.Authentication.BadBindException;
import org.exchange.library.Exception.GlobalException;
import org.exchange.library.Mapper.ValidationErrorMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.server.ServerWebInputException;

@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {

        if (e instanceof GlobalException exception) {
            return ResponseEntity.status(exception.getStatus())
                    .body(ErrorResponse.builder()
                            .errorCode(exception.getErrorCode())
                            .message(e.getMessage())
                            .build());
        }
        else if (e instanceof MethodNotAllowedException || e instanceof ServerWebInputException)
        {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                    .body(ErrorResponse.builder()
                            .errorCode(Error.INVALID_REQUEST_METHOD_OR_VALUE)
                            .message("Invalid request made!")
                            .build());
        }
        else if (e instanceof BindException bindException)
        {
            BindingResult result = bindException.getBindingResult();
            BadBindException badBindException = ValidationErrorMapper.fetchFirstError(result);

            return ResponseEntity.status(badBindException.getStatus())
                    .body(ErrorResponse.builder()
                            .errorCode(badBindException.getErrorCode())
                            .message(badBindException.getMessage())
                            .build());
        }
        /* Todo: IMPLEMENT LOGGING */
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.builder()
                        .errorCode("INTERNAL_SERVER_ERROR")
                        .message("An unexpected error occurred!")
                        .build());

    }
}
