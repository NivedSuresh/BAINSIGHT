package org.exchange.order.Service;

import org.exchange.library.Dto.Order.OrderDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PersistentService {

    Mono<Void> cancelOrder(String broker, Long id);

    Flux<OrderDto> getAllOrders();
}
