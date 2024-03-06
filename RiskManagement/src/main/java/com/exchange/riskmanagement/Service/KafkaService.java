package com.exchange.riskmanagement.Service;

import org.exchange.library.KafkaEvent.OrderEvent;

public interface KafkaService {
    void persistVerifiedOrder(OrderEvent event);

}
