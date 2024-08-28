package com.lunasapiens.config;

import java.security.Principal;


public class CustomPrincipalWebSocketChatBot implements Principal {

    private final String name;
    private final String ipAddress; // Nuovo campo per l'IP Address

    // Costruttore aggiornato per accettare anche l'IP Address
    public CustomPrincipalWebSocketChatBot(String name, String ipAddress) {
        this.name = name;
        this.ipAddress = ipAddress;
    }

    @Override
    public String getName() {
        return name;
    }

    // Getter per l'IP Address
    public String getIpAddress() {
        return ipAddress;
    }


}

