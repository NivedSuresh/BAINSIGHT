package org.exchange.library.Exception.Kafka;

import lombok.Getter;
import org.exchange.library.Advice.Error;
import org.exchange.library.Exception.GlobalException;
import org.exchange.library.KafkaEvent.OrderEvent;
import org.springframework.http.HttpStatus;

@Getter
public class KafkaOrderRejectionException extends GlobalException {
    private OrderEvent rejectionEvent;

    public KafkaOrderRejectionException(String message, HttpStatus status, String errorCode) {
        super(message, status, errorCode);
    }

    public KafkaOrderRejectionException(OrderEvent rejectionEvent) {
        super(
                "Failed to log order rejection event",
                HttpStatus.INTERNAL_SERVER_ERROR,
                Error.KAFKA_ORDER_REJECTION_FAILURE
        );
        this.rejectionEvent = rejectionEvent;
    }
}
