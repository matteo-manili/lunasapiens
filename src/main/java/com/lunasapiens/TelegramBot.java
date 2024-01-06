package com.lunasapiens;



import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
public class TelegramBot extends TelegramLongPollingBot {

    /*
     * Non utilizzare questo metodo per conservare username e token
     */

    // https://t.me/LunaSapiensUser_bot
    private final static String USERNAME = "LunaSapiensUser_bot";
    private final static String TOKEN = "6410764803:AAFl2i4OYn2htKW7H4Wd2mgZLybd0pC2jas";


    public String getBotUsername() {
        return USERNAME;
    }

    @Override
    public String getBotToken() {
        return TOKEN;
    }

    public void onUpdateReceived(Update update) {

        System.out.println("weewewweweewweweew");

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            if ("/start".equals(messageText)) {
                inviaMessaggio(chatId.toString(), "Ciao! Il tuo chatId è: " + chatId);
            } else {
                // Gestisci altri tipi di messaggi o comandi
            }
        }

        // do stuff
    }


    public void inviaMessaggio(String chatId, String testoMessaggio) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(testoMessaggio);

        try {
            execute(message);

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


}

