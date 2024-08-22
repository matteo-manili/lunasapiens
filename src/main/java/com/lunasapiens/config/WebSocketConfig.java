package com.lunasapiens.config;

import com.lunasapiens.service.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.net.InetSocketAddress;
import java.security.Principal;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);

    @Autowired
    JwtService jwtService;


    public static final String userAnonymous = "anonymous";


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

                        InetSocketAddress remoteAddress = request.getRemoteAddress();
                        String ipAddress = (remoteAddress != null) ? remoteAddress.getAddress().getHostAddress() : "unknown";

                        // Recupera l'utente autenticato da Spring Security
                        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                        if (authentication != null && authentication.isAuthenticated()) {
                            Object principal = authentication.getPrincipal();
                            // Verifica se il principal è un'istanza di un tuo oggetto utente
                            if (principal instanceof UserDetails) {
                                UserDetails userDetails = (UserDetails) principal;
                                return new CustomPrincipalWebSocket(userDetails.getUsername(), ipAddress); // Usa il nome utente come identificativo
                            }
                        }

                        // Se non c'è un'utente autenticato, puoi decidere come gestire la situazione
                        return new CustomPrincipalWebSocket(userAnonymous, ipAddress); // Utente anonimo, ad esempio
                    }
                })
                .withSockJS();
    }



}

