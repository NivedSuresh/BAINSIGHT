package com.exchange.riskmanagement.Service.impls;

import com.exchange.riskmanagement.Mapper.Mapper;
import com.exchange.riskmanagement.Service.KafkaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.exchange.library.Exception.Kafka.KafkaOrderAdditionException;
import org.exchange.library.KafkaEvent.OrderEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaServiceImpl implements KafkaService {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Mapper mapper;

    @Override
    public void persistVerifiedOrder(OrderEvent event) {
        kafkaTemplate.send("add-order", event)
                .whenComplete((stringOrderSendResult, throwable) -> {
                    if (throwable != null) {
                        log.error("An Exception occurred while trying to update the Order Book, - message: {} - (IGNORE IF EMPTY)", throwable.getMessage());
                        throw new KafkaOrderAdditionException(event);
                    }
                    log.info("Order has been added to the book!");
                });
    }

}
