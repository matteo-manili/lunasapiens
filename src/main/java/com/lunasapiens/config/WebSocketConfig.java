package com.lunasapiens.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {



    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {

        // Configura i prefissi per i messaggi dell'applicazione
        config.setApplicationDestinationPrefixes("/app");

        // Configura i prefissi per i messaggi diretti agli utenti
        config.setUserDestinationPrefix("/user");

        // Configura i prefissi per i messaggi gestiti dal broker
        config.enableSimpleBroker("/topic/", "/queue/");

    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chat-websocket").withSockJS();
    }


}

