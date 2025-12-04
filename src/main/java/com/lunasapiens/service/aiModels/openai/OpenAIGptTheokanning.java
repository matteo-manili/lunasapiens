package com.lunasapiens.service.aiModels.openai;


import com.theokanning.openai.service.OpenAiService;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Misuratore di tokens per domanda. Copia e incolla la domanda e ti dici quanti token vengono calcoltati https://platform.openai.com/tokenizer
 *
 * gpt-4o	New GPT-4o
 * Our most advanced, multimodal flagship model that’s cheaper and faster than GPT-4 Turbo. Currently points to gpt-4o-2024-05-13.	128,000 tokens
 * PREZZO gpt-4o input: 5,00 USD/1M tokens. Output: 15,00 USD/1M tokens
 *
 * gpt-3.5-turbo-0125	The latest GPT-3.5 Turbo model with higher accuracy at responding in requested formats and a fix for a bug which caused a
 * text encoding issue for non-English language function calls. Returns a maximum of 4,096 output tokens. Learn more.	16,385 tokens
 * PREZZO gpt-3.5-turbo-0125 input: 0,50 USD/1M tokens. Output: 1,50 USD/1M tokens
 *
 * gpt-3.5-turbo-instruct Similar capabilities as GPT-3 era models. Compatible with legacy Completions endpoint and not Chat Completions.
 * PREZZO gpt-3.5-turbo-instruct input: 1,50 USD/1M tokens. Output: 2,00 USD/1M tokens
 */
public class OpenAIGptTheokanning {

    private static final Logger logger = LoggerFactory.getLogger(OpenAIGptTheokanning.class);


    /**
     * Calcola il numero totale di token per una lista di messaggi.
     * La regola empirica utilizzata è che un token corrisponde a circa 4 caratteri.
     *
     * @param messages Lista di messaggi contenenti il testo da tokenizzare.
     * @return Il numero totale di token stimato.
     */
    public static int calculateTokenCount(List<ChatMessage> messages, int tokensRisposta, Double finalcaratteriPerTokenStima) {
        int totalCharacterCount = 0;
        // Calcola il numero totale di caratteri in tutti i messaggi
        for (ChatMessage message : messages) {
            if (message.getContent() != null) {
                totalCharacterCount += message.getContent().length();
            }
        }
        // Applica la regola empirica: 1 token = 4.0 caratteri e aggiungo i tokens per la risposta
        int tokensTotali = (int) Math.ceil(totalCharacterCount / finalcaratteriPerTokenStima) + tokensRisposta;
        logger.info( "totalCharacterCount: "+totalCharacterCount + " | tokensTotali: "+tokensTotali );
        return tokensTotali;
    }


    public StringBuilder eseguiOpenAIGptTheokanning(String apiKey, double temperature, int tokensAggiuntiPerRisposta, double caratteriPerTokenStima,
                                                    final String modelGpt, List<ChatMessage> chatMessageList) {
        // Inizializza il servizio OpenAI con il client configurato
        OpenAiService service = new OpenAiService(apiKey, Duration.ofSeconds(30));
        return eseguiOpenAIGptTheokanning(service, temperature, tokensAggiuntiPerRisposta, caratteriPerTokenStima, modelGpt, chatMessageList) ;
    }



    public StringBuilder eseguiOpenAIGptTheokanning(String apiKey, double temperature, int tokensAggiuntiPerRisposta, Double caratteriPerTokenStima, final String modelGpt,
                                                    Integer timeoutSecondiRisposta, String domanda) {
        // Inizializza il servizio OpenAI con il client configurato
        OpenAiService service = new OpenAiService(apiKey, Duration.ofSeconds(timeoutSecondiRisposta));
        // Inizializza la lista dei messaggi
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage("system", domanda /*"Sei un astrologo che genera."*/ ));
        //messages.add(new ChatMessage("user", domanda));

        return eseguiOpenAIGptTheokanning(service, temperature, tokensAggiuntiPerRisposta, caratteriPerTokenStima, modelGpt, messages ) ;
    }




    /**
     * chatgpt 3.5 e 4.0 accetta ruoli di user, system e assistant. gpt-3.5-turbo-instruct non vuole nessu ruolo
     * chatgpt 3.5 e 4.0 vogliono system e user nella domanda
     */
    private StringBuilder eseguiOpenAIGptTheokanning(OpenAiService service, double temperature, int tokensAggiuntiPerRisposta, double caratteriPerTokenStima,
                                                     String modelGpt, List<ChatMessage> chatMessageList) {

        logger.info("################### INIZIOOO eseguiOpenAiTheokanning "+ modelGpt +" ###################");

        int maxTokens = calculateTokenCount(chatMessageList, tokensAggiuntiPerRisposta, caratteriPerTokenStima);

        // Costruisci la richiesta di completamento
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model( modelGpt.trim() ) // Assicurati di utilizzare il modello corretto
                .messages(chatMessageList)
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
            logger.info("risposta: " + risposta.toString());

        } catch (Exception e) {
            System.err.println("Errore durante la richiesta: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("FINEEEEEEEEE eseguiOpenAiTheokanning "+ modelGpt +" ###################: ");

        return risposta;
    }


}

