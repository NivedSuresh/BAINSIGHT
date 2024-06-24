package org.bainsight.updates.WebSocket;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
class WebSocketConfig implements WebSocketMessageBrokerConfigurer{

    @Value("${allowed.origin:http://localhost:4200}")
    private String allowedOrigin;
    private final UserHandShakeHandler userHandShakeHandler;


    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(final StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins(allowedOrigin)
                .setHandshakeHandler(userHandShakeHandler)
                .withSockJS();

        registry.addEndpoint("/ws") // Alternative endpoint if needed
                .setHandshakeHandler(userHandShakeHandler)
                .setAllowedOrigins(allowedOrigin); // Add interceptor here
    }
}
