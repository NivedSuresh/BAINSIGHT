package com.exchange.riskmanagement.Config.Stomp;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.exchange.library.Dto.Order.OrderRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class StompHandler extends TextWebSocketHandler {
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        OrderRequest update = new ObjectMapper().readValue(payload, OrderRequest.class);
        System.out.println(update);
    }
}
