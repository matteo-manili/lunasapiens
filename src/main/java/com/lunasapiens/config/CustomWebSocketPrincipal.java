package com.lunasapiens.config;



import java.security.Principal;

public class CustomWebSocketPrincipal implements Principal {

    private final String name;
    private final String ipAddress;

    public CustomWebSocketPrincipal(String name, String ipAddress) {
        this.name = name;
        this.ipAddress = ipAddress;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getIpAddress() {
        return ipAddress;
    }
}

