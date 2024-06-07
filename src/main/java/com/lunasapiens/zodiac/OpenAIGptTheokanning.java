package com.lunasapiens.zodiac;


import com.theokanning.openai.service.OpenAiService;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class OpenAIGptTheokanning {

    private static final Logger logger = LoggerFactory.getLogger(OpenAIGptTheokanning.class);


    /**
     * chatgpt 3.5 e 4.0 accetta ruoli di user, system e assistant. gpt-3.5-turbo-instruct non vuole nessu ruolo
     * chatgpt 3.5 e 4.0 vogliono system e user nella domanda
     */
    public StringBuilder eseguiOpenAIGptTheokanning(String apiKey, int maxTokens, double temperature, String domanda, final String modelGpt) {


        System.out.println("INIZIOOO eseguiOpenAiTheokanning "+ modelGpt +"###################: ");

        Duration timeout = Duration.ofSeconds(30);

        // Inizializza il servizio OpenAI con il client configurato
        OpenAiService service = new OpenAiService(apiKey, timeout);

        // Inizializza la lista dei messaggi
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage("system", "Sei un astrologo esperto."));
        messages.add(new ChatMessage("user", domanda));

        // Costruisci la richiesta di completamento
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model( modelGpt ) // Assicurati di utilizzare il modello corretto
                .messages(messages)
                .maxTokens(maxTokens)
                .temperature(temperature)
                .build();

        StringBuilder risposta = new StringBuilder();

        try {
            // Invia la richiesta e ottieni la risposta
            List<ChatCompletionChoice> choices = service.createChatCompletion(request).getChoices();


            // Processa e stampa la risposta
            for (ChatCompletionChoice choice : choices) {
                risposta.append(choice.getMessage().getContent());
                //System.out.printf("Index: %d, Message: %s\n", choice.getIndex(), choice.getMessage().getContent());
            }
            System.out.println("risposta: " + risposta.toString());


        } catch (Exception e) {
            System.err.println("Errore durante la richiesta: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("FINEEEEEEEEE eseguiOpenAiTheokanning "+ modelGpt +"###################: ");

        return risposta;

    }
}

