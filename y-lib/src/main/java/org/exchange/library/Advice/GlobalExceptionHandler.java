package org.exchange.library.Advice;


import org.exchange.library.Exception.GlobalException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        if(e instanceof GlobalException exception){
            return ResponseEntity.status(exception.getStatus())
                    .body(ErrorResponse.builder()
                            .errorCode(exception.getErrorCode())
                            .message(e.getMessage())
                            .build()
                    );
        }

        /* Todo: IMPLEMENT LOGGING */


        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ErrorResponse.builder()
                                .errorCode("This service is unavailable right now, please try again later!")
                                .message(e.getMessage())
                                .build());
    }
}
