package org.exchange.order.Repository;


import org.exchange.order.Model.Order;
import org.exchange.order.Model.OrderInfo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface OrderRepo extends R2dbcRepository<Order, Long> {
    @Query("SELECT o.*, m.* FROM orders o LEFT JOIN matches m ON o.order_id = m.order_id WHERE o.order_id = :orderId")
    Mono<OrderInfo> findOrderAndMatchesByOrderId(UUID orderId);
}
