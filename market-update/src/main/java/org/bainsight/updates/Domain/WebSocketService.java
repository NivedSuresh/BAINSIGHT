package org.bainsight.updates.Domain;

import lombok.RequiredArgsConstructor;
import org.exchange.library.Dto.MarketRelated.CandleStick;
import org.exchange.library.Dto.Notification.Notification;
import org.exchange.library.Dto.Notification.Ucc;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@EnableScheduling
public class WebSocketService {

    private final SimpMessagingTemplate template;

    public void pushRequested(final CandleStick candleStick){
        this.template.convertAndSend("/topic/" + candleStick.getSymbol() , candleStick);
    }


    public void pushNotification(final String ucc, final Notification<? extends Ucc> notification){
        this.template.convertAndSendToUser(ucc, "/topic/private-notifications", notification);
    }


}
