package org.bainsight.updates.Service;

import lombok.RequiredArgsConstructor;
import org.exchange.library.Dto.MarketRelated.CandleStick;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WebSocketService {

    private final SimpMessagingTemplate template;

    public void pushRequested(final CandleStick candleStick){
        this.template.convertAndSend("/topic/" + candleStick.getSymbol() , candleStick);
    }


}
