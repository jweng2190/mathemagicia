package com.example.conf;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.standard.TomcatRequestUpgradeStrategy;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

@Configuration
@EnableWebSocketMessageBroker
public class WebsocketConfiguration implements WebSocketMessageBrokerConfigurer {
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/connect", "/ready", "/game_answer", "/end", "/chat", "/rematch", "/status", "/bot")
        //.setHandshakeHandler(new DefaultHandshakeHandler(new TomcatRequestUpgradeStrategy()))
        .setAllowedOriginPatterns("*");
        registry.addEndpoint("/connect", "/ready", "/game_answer", "/end", "/chat", "/rematch", "/status", "/bot")
                .setAllowedOriginPatterns("*").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/queue", "/topic", "/user");
        registry.setApplicationDestinationPrefixes("/ws");
    }
}