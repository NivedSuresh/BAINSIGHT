package org.exchange.order.Service.impls;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.exchange.library.Dto.Order.OrderDto;
import org.exchange.library.Dto.Order.OrderRequest;
import org.exchange.library.Enums.OrderStatus;
import org.exchange.library.Enums.OrderType;
import org.exchange.library.Exception.Order.NonExistentOrderException;
import org.exchange.order.Mapper.Mapper;
import org.exchange.order.Model.Order;
import org.exchange.order.Repository.OrderRepo;
import org.exchange.order.Service.PersistentService;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@EnableCaching
@RequiredArgsConstructor
@Slf4j
public class PersistentServiceImpl implements PersistentService {

    private final WebClient webClient;
    private final Mapper mapper;
    private final OrderRepo orderRepo;


    @Override
    public Flux<OrderDto> getAllOrders() {
        return orderRepo.findAll()
                .map(mapper::entityToOrderDto);
    }

    private OrderStatus getOrderStatus(OrderRequest request) {
        return request.type() == OrderType.ORDER_TYPE_MARKET ? OrderStatus.PENDING : OrderStatus.ACCEPTED;
    }


    /* A call to the repository is made to persist the Order */
    private Mono<Void> persistOrder(OrderVerifiedEvent verifiedEvent) {
        Order entity = mapper.verifiedEventToOrderEntity(verifiedEvent);
        return orderRepo.save(entity)
                .flatMap(order ->
                        updateBroker(order.getBroker(),
                                order.getOrderId(),
                                order.getBroker_order_id(),
                                order.getStatus()
                        ));
    }

    private Mono<? extends Void> updateBroker(String broker, Long id, String brokerOrderId, OrderStatus status) {
        return Mono.empty();
    }


    /* The Order status is updated in the database as cancelled */
    @Override
    public Mono<Void> cancelOrder(String broker, Long id) {
        return orderRepo.updateOrderStatusAndLastUpdated(id, OrderStatus.CANCELLED, broker)
                .flatMap(count -> count != 1 ? Mono.error(NonExistentOrderException::new) : updateBroker(broker, id, null, OrderStatus.CANCELLED));
    }


}
