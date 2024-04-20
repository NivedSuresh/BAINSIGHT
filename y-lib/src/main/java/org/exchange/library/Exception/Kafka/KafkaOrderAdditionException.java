package org.exchange.library.Exception.Kafka;

import lombok.Getter;
import org.exchange.library.Advice.Error;
import org.exchange.library.KafkaEvent.OrderEvent;

@Getter
public class KafkaOrderAdditionException extends KafkaException {
    private final OrderEvent event;

    public KafkaOrderAdditionException(OrderEvent event) {
        super("Order forwarding has failed as the queue is unavailable",
                Error.KAFKA_ORDER_BOOK_ADDITION_FAILURE);
        this.event = event;
    }
}
