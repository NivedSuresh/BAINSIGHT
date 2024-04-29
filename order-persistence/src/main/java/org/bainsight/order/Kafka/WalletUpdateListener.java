package org.bainsight.order.Kafka;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bainsight.order.Exception.MatchNotFoundException;
import org.bainsight.order.GrpcService.OrderPersistance.GrpcOrderService;
import org.bainsight.order.GrpcService.OrderPersistance.MatchRepo;
import org.bainsight.order.Model.Entity.Match;
import org.bainsight.order.Model.Entity.Order;
import org.exchange.library.Dto.Order.MarketBidStatus;
import org.exchange.library.Enums.MatchStatus;
import org.exchange.library.Exception.Order.NotEnoughBalanceException;
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
    private final ObjectMapper objectMapper;

    @Transactional
    @KafkaListener(topics = "wallet-validation-update", groupId = "order-persistence")
    public void walletUpdates(String walletUpdate)
    {
        MarketBidStatus marketBidStatus = null;
       try
       {
           marketBidStatus = this.objectMapper.readValue(walletUpdate, MarketBidStatus.class);
           log.info("Match validated for Market_BID: {}", walletUpdate);

           Optional<Match> optional = this.matchRepo.findById(marketBidStatus.getMatchId());
           if(optional.isEmpty()) throw new MatchNotFoundException(marketBidStatus.getMatchId());
           Match match = optional.get();
           log.info("Match fetched: {}", match);

           if(marketBidStatus.getMatchStatus() == MatchStatus.REJECTED){
               match.setMatchStatus(marketBidStatus.getMatchStatus());
               this.matchRepo.save(match);
               throw new NotEnoughBalanceException();
           }

           match.setWasValidated(true);
           Order order = this.orderService.findOrderById(match.getOrderId());
           log.info("Order fetched: {}", order);

           orderService.updateOrderAfterMatch(order ,match);
       }
       catch (RuntimeException | JsonProcessingException ex)
       {
           if(ex instanceof NotEnoughBalanceException){
               this.rejectMatch(marketBidStatus);
           }
           log.error(ex.getMessage());
           /* TODO: IMPLEMENT JOURNALING */
       }
    }

    private void rejectMatch(MarketBidStatus marketBidStatus) {

    }


}
