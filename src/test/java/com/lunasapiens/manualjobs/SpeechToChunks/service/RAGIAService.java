package com.lunasapiens.manualjobs.SpeechToChunks.service;

import com.lunasapiens.config.AppConfig;
import com.lunasapiens.zodiac.OpenAIGptTheokanning;
import com.theokanning.openai.completion.chat.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class RAGIAService {

    private static final Logger logger = LoggerFactory.getLogger(RAGIAService.class);

    @Autowired
    private AppConfig appConfig;


    // tokensRisposta signfiica i token da aggiungere oltre i token per la domanda
    private Double temperature = 0.2;
    private final Double finalcaratteriPerTokenStima = 10.0;
    private Integer tokensAggiuntiPerRisposta = 1000;

    /**
     * i tokensAggiuntiPerRisposta corrispondno al numero di parole del testo. in questo modo la risposta della LLM è completa.
     * cioè mette la punteggiatura a tutto il testo senza troncare il testo.
     */
    public StringBuilder chiediAlloPsicologo(List<ChatMessage> chatMessageList) {
        //########################################## INIZIO - INVIO LA DOMANDA ALLA IA #########################

        OpenAIGptTheokanning openAIGptTheokanning = new OpenAIGptTheokanning();


        return openAIGptTheokanning.eseguiOpenAIGptTheokanning(appConfig.getParamOpenAi().getApiKeyOpenAI(), temperature, tokensAggiuntiPerRisposta, finalcaratteriPerTokenStima,
                appConfig.getParamOpenAi().getModelGpt4_Mini(), chatMessageList );



    }







}




