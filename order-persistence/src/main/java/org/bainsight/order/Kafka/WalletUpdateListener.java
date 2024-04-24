package org.bainsight.order.Kafka;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bainsight.order.Exception.MatchNotFoundException;
import org.bainsight.order.GrpcService.OrderPersistance.GrpcOrderService;
import org.bainsight.order.GrpcService.OrderPersistance.MatchRepo;
import org.bainsight.order.Model.Entity.Match;
import org.bainsight.order.Model.Entity.Order;
import org.exchange.library.Exception.BadRequest.InvalidStateException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletUpdateListener {

    private final GrpcOrderService orderService;
    private final MatchRepo matchRepo;

    @Transactional
    @KafkaListener(topics = "wallet-validation-update", groupId = "order-persistence")
    public void walletUpdates(String matchIdAsString)
    {
       try
       {
           log.info("Match validated for Market_BID: {}", matchIdAsString);
           Long matchId = Long.parseLong(matchIdAsString);
           Optional<Match> optional = this.matchRepo.findById(matchId);
           if(optional.isEmpty()) throw new MatchNotFoundException(matchIdAsString);
           Match match = optional.get();
           log.info("Match fetched: {}", match);

           Order order = this.orderService.findOrderById(match.getOrderId());
           log.info("Order fetched: {}", order);

           orderService.updateOrderAfterMatch(order ,match);
       }
       catch (RuntimeException ex)
       {
           log.error(ex.getMessage());
           /* TODO: IMPLEMENT JOURNALING */
       }
    }



}
