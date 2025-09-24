package com.lunasapiens.manualjobs.SpeechToChunks.service;

import com.lunasapiens.config.AppConfig;

import com.lunasapiens.zodiac.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class PunteggiaturaTestoIAService {

    private static final Logger logger = LoggerFactory.getLogger(PunteggiaturaTestoIAService.class);

    @Autowired
    private AppConfig appConfig;


    // tokensRisposta signfiica i token da aggiungere oltre i token per la domanda
    private Double temperature = 0.2;
    private Integer tokensAggiuntiPerRisposta = 2000;
    private final Double finalcaratteriPerTokenStima = 10.0;
    private Integer timeoutSecondiRisposta = 200;



    public StringBuilder punteggiaturaTesto(String testo) {
        //########################################## INIZIO - INVIO LA DOMANDA ALLA IA #########################
        /*
        OpenAIGptTheokanning we = new OpenAIGptTheokanning();
        we.eseguiOpenAIGptTheokanning(appConfig.getParamOpenAi().getApiKeyOpenAI(), maxTokens, temperature, domandaBuilder.toString(),
                appConfig.getParamOpenAi().getModelGpt4() );

        we.eseguiOpenAIGptTheokanning(appConfig.getParamOpenAi().getApiKeyOpenAI(), maxTokens, temperature, domandaBuilder.toString(),
                appConfig.getParamOpenAi().getModelGpt3_5());
         */

        StringBuilder inputPrompt = input_prompt(testo);
        //logger.info("DOMANDA: " + domanda);

        //OpenAIGptAzure openAIGptAzure = new OpenAIGptAzure();
        //return openAIGptAzure.eseguiOpenAIGptAzure_Instruct(appConfig.getParamOpenAi().getApiKeyOpenAI(), maxTokens, temperature, domanda.toString(),
        //      appConfig.getParamOpenAi().getModelGpt3_5TurboInstruct() );

        // return openAIGptTheokanning.eseguiOpenAIGptTheokanning(appConfig.getParamOpenAi().getApiKeyOpenAI(), maxTokens, temperature, domanda.toString(),
        //                appConfig.getParamOpenAi().getModelGpt4() );

        OpenAIGptTheokanning openAIGptTheokanning = new OpenAIGptTheokanning();
        return openAIGptTheokanning.eseguiOpenAIGptTheokanning(appConfig.getParamOpenAi().getApiKeyOpenAI(), temperature, tokensAggiuntiPerRisposta, finalcaratteriPerTokenStima,
                appConfig.getParamOpenAi().getModelGpt4_Mini(), timeoutSecondiRisposta, inputPrompt.toString() );

    }


    public StringBuilder input_prompt(String testo) {


        StringBuilder domandaBuilder = new StringBuilder();
        domandaBuilder.append("inserisci la punteggiatura a questo testo senza togliere e modificare le parole: ");

        // il testo a cui mettere la punteggiarura
        domandaBuilder.append(testo);


        return domandaBuilder;
    }




}




