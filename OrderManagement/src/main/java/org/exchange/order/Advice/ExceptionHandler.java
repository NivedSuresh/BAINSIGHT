package org.exchange.order.Advice;

import org.exchange.library.Advice.Error;
import org.exchange.library.Advice.ErrorResponse;
import org.exchange.library.Exception.GlobalException;
import org.exchange.library.Exception.Kafka.KafkaException;
import org.exchange.library.Exception.Kafka.KafkaOrderAdditionException;
import org.exchange.library.Exception.Kafka.KafkaOrderCancellationException;
import org.exchange.library.KafkaEvent.OrderEvent;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class ExceptionHandler {
    @org.springframework.web.bind.annotation.ExceptionHandler(GlobalException.class)
    public ResponseEntity<ErrorResponse> handleException(GlobalException e) {
        return ResponseEntity.status(e.getStatus())
                .body(ErrorResponse.builder()
                        .errorCode(e.getErrorCode())
                        .message(e.getMessage())
                        .build()
                );
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<Mono<ErrorResponse>> handleException(KafkaException e) {
        if (e instanceof KafkaOrderAdditionException exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(logKafkaOrderForwardingException(exception.getEvent()));
        }
        if (e instanceof KafkaOrderCancellationException exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(logKafkaOrderCancellation(exception.getId()));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Mono.just(ErrorResponse.builder()
                        .errorCode(Error.UNKNOWN_KAFKA_EXCEPTION)
                        .message(e.getMessage()).build()
                ));
    }

    private Mono<ErrorResponse> logKafkaOrderCancellation(Long id) {
        return null;
    }

    private Mono<ErrorResponse> logKafkaOrderForwardingException(OrderEvent order) {
        return null;
    }

}
