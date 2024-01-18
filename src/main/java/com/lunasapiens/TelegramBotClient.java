package com.lunasapiens;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/*
Questo passaggio è obbligatorio poiché creerà un chat_id in background per la tua comunicazione privata con il tuo bot in background.
Abbiamo bisogno di questo ID per inviare la notifica da Java esattamente a questa chat.

Per recuperare questo chat_id, visita il seguente endpoint: https://api.telegram.org/bot$TOKEN/getUpdates
https://api.telegram.org/bot6410764803:AAFl2i4OYn2htKW7H4Wd2mgZLybd0pC2jas/getUpdates
(sostituisci prima $TOKEN con il tuo token) con Postman o il tuo browser (è HTTP GET).

risultato:
{"ok":true,"result":[{"update_id":388311441,
"message":{"message_id":13,"from":{"id":1034805278,"is_bot":false,"first_name":"Matte","username":"MatteoBcn81","language_code":"it"},"chat":{"id":1034805278,"first_name":"Matte","username":"MatteoBcn81","type":"private"},"date":1704505011,"text":"/start","entities":[{"offset":0,"length":6,"type":"bot_command"}]}},{"update_id":388311442,
"message":{"message_id":14,"from":{"id":1034805278,"is_bot":false,"first_name":"Matte","username":"MatteoBcn81","language_code":"it"},"chat":{"id":1034805278,"first_name":"Matte","username":"MatteoBcn81","type":"private"},"date":1704505190,"text":"Ffgf"}},{"update_id":388311443,
"message":{"message_id":15,"from":{"id":1034805278,"is_bot":false,"first_name":"Matte","username":"MatteoBcn81","language_code":"it"},"chat":{"id":1034805278,"first_name":"Matte","username":"MatteoBcn81","type":"private"},"date":1704505207,"text":"Ghggg"}}]}
 */

@Component
public class TelegramBotClient extends TelegramLongPollingBot {

    // https://t.me/LunaSapiensUser_bot

    @Value("${api.telegram.bot.username}")
    private String telegramBotUsername;

    @Value("${api.telegram.token}")
    private String telegramToken;

    @Value("${api.telegram.chatId}")
    private String telegramChatId;


    /*
    public TelegramBot()  {
        // Disabilita il webhook
        try {
            clearWebhook();
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }
    */

    @Override
    public String getBotUsername() {
        return telegramBotUsername;
    }

    @Override
    public String getBotToken() {
        return telegramToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        // Gestisci gli aggiornamenti qui, se necessario
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
           System.out.println("chatId: "+chatId+" messageText: "+messageText);
        }
    }

    public void inviaMessaggio(String testoMessaggio) {
        SendMessage message = new SendMessage();
        message.setChatId( telegramChatId );
        message.setText(testoMessaggio);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }



}