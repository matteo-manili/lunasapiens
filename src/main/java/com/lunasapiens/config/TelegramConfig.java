package com.lunasapiens.config;

public class TelegramConfig {


    private String token;
    private String chatId;
    private String username;


    public TelegramConfig(String token, String chatId, String username) {
        this.token = token;
        this.chatId = chatId;
        this.username = username;
    }


    public String getToken() {
        return token;
    }

    public String getChatId() {
        return chatId;
    }

    public String getUsername() {
        return username;
    }
}
