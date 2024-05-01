package org.bainsight.order.Listeners;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bainsight.order.Data.OrderPersistance.GrpcOrderService;
import org.bainsight.order.Model.Events.OrderMatch;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderMatchListener {

    private final GrpcOrderService grpcOrderService;
    private final ExecutorService greenExecutor;

    /**
     * This message here can listen to order updates from exchange.
     * IRL this would be done through fix/websocket/rest/custom protocol
     * */
    public void updateOnMatch(OrderMatch match) {
        log.info("New Match: {}", match);
        greenExecutor.execute(() -> this.grpcOrderService.processMatch(match));
    }



}
