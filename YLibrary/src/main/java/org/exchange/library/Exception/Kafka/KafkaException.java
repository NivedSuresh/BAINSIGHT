package org.exchange.library.Exception.Kafka;

import lombok.Getter;

@Getter
public class KafkaException extends RuntimeException {
    private final String errorCode;

    public KafkaException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
