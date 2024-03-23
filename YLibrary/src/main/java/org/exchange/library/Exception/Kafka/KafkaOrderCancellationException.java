package org.exchange.library.Exception.Kafka;

import lombok.Getter;
import org.exchange.library.Advice.Error;

@Getter
public class KafkaOrderCancellationException extends KafkaException {
    private final Long id;

    public KafkaOrderCancellationException(Long id) {
        super("Failed forwarding Order cancellation request to Order Book but is updated",
                Error.KAFKA_ORDER_BOOK_CANCELLATION_FAILURE);
        this.id = id;
    }
}
