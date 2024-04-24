package org.bainsight.order.MatchingSim;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bainsight.OrderType;
import org.bainsight.order.GrpcService.OrderPersistance.GrpcOrderService;
import org.bainsight.order.Listeners.OrderMatchListener;
import org.bainsight.order.MatchingSim.Entity.CandleStick;
import org.bainsight.order.MatchingSim.Repo.CandleStickRepo;
import org.bainsight.order.Model.Entity.Order;
import org.bainsight.order.Model.Events.OrderMatch;
import org.exchange.library.Enums.OrderStatus;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@EnableScheduling
@Service
@RequiredArgsConstructor
@Slf4j
public class MatchingJob {

    private final GrpcOrderService grpcOrderService;
    private final CandleStickRepo candleStickRepo;
    private final OrderMatchListener orderMatchListener;
    private final Random random = new Random();

    @Scheduled(fixedRate = 2000)
    public void matchAllMarketOrders()
    {
        List<Order> openMarketOrders = this.grpcOrderService.findAllByOrderTypeAndOrderStatus(OrderType.ORDER_TYPE_MARKET, OrderStatus.OPEN);

        log.info("Orders count: {}", openMarketOrders.size());
        for(Order order : openMarketOrders)
        {

            log.info("Order: {}", order);

            Optional<CandleStick> optional = this.candleStickRepo.findById(order.getSymbol());
            CandleStick stick = optional.get();
            double matchPrice = ((stick.getHigh() + stick.getLow()) / 2) + random.nextDouble(-2, 2);


            OrderMatch orderMatch = OrderMatch.builder()
                    .ucc(order.getUcc().toString())
                    .matchTime(LocalDateTime.now())
                    .symbol(order.getSymbol())
                    .exchange(order.getExchange())
                    .priceMatchedFor(matchPrice)
                    .orderId(order.getOrderId().toString())
                    .matchedQuantity(order.getQuantityRequested())
                    .quantityRequested(order.getQuantityRequested())
                    .build();

            this.orderMatchListener.updateOnMatch(orderMatch);

        }

    }

    @Scheduled(fixedRate = 2000)
    public void matchAllLimitOrders()
    {
        List<Order> openMarketOrders = this.grpcOrderService.findAllByOrderTypeAndOrderStatus(OrderType.ORDER_TYPE_LIMIT, OrderStatus.OPEN);

        for(Order order : openMarketOrders)
        {

            long matched = order.getQuantityRequested() - order.getQuantityMatched();
            matched = Math.max(1, matched);
            OrderMatch orderMatch = OrderMatch.builder()
                    .ucc(order.getUcc().toString())
                    .matchTime(LocalDateTime.now())
                    .symbol(order.getSymbol())
                    .exchange(order.getExchange())
                    .priceMatchedFor(order.getPrice())
                    .orderId(order.getOrderId().toString())
                    .matchedQuantity(matched)
                    .quantityRequested(order.getQuantityRequested())
                    .build();


            this.orderMatchListener.updateOnMatch(orderMatch);

        }

    }


}