package com.lunasapiens.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;

import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {


    /**
     * Configura il message broker per la comunicazione WebSocket:
     * - Imposta i prefissi per i messaggi destinati all'applicazione.
     * - Imposta il prefisso per i messaggi destinati agli utenti specifici.
     * - Abilita il broker per i messaggi su topic e code.
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {

        // Configura i prefissi per i messaggi dell'applicazione
        config.setApplicationDestinationPrefixes("/app");

        // Configura i prefissi per i messaggi diretti agli utenti
        config.setUserDestinationPrefix("/user");

        // Configura i prefissi per i messaggi gestiti dal broker
        config.enableSimpleBroker("/topic/", "/queue/");
    }

    /**
     * Registra l'endpoint STOMP per le connessioni WebSocket:
     * - Aggiunge un endpoint "/chat-websocket" per le connessioni WebSocket.
     * - Utilizza un handshake handler per creare un token di autenticazione anonimo.
     * - Abilita SockJS come fallback per i client che non supportano WebSocket.
     *
     * - il Principal non lo sto usando ma potrebbe essere utile per gestire gli utenti del webSocket
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chat-websocket")
                .setHandshakeHandler(new DefaultHandshakeHandler() {
                    @Override
                    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
                        String uniqueId = UUID.randomUUID().toString();
                        //return new AnonymousAuthenticationToken(uniqueId, uniqueId, AuthorityUtils.createAuthorityList("ROLE_USER"));
                        return null;
                    }
                })
                .withSockJS();
    }





}

