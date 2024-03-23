package org.exchange.user.ExceptionHandler;


import org.exchange.library.Advice.ErrorResponse;
import org.exchange.library.Exception.GlobalException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<ErrorResponse> handleException(GlobalException e) {
        return ResponseEntity.status(e.getStatus())
                .body(ErrorResponse.builder()
                        .errorCode(e.getErrorCode())
                        .message(e.getMessage())
                        .build()
                );
    }
}
