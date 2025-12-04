package com.lunasapiens.manualjobs.SpeechToChunks.service;

import com.lunasapiens.config.AppConfig;
import com.lunasapiens.service.aiModels.openai.OpenAIGptTheokanning;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class FaiSintesiIAService {

    private static final Logger logger = LoggerFactory.getLogger(FaiSintesiIAService.class);

    @Autowired
    private AppConfig appConfig;


    // tokensRisposta signfiica i token da aggiungere oltre i token per la domanda
    private Double temperature = 0.2;
    private final Double finalcaratteriPerTokenStima = 10.0;
    private Integer timeoutSecondiRisposta = 500; // secondi attesa riposta


    /**
     */
    public StringBuilder generaSintesi(String testo, Integer tokensAggiuntiPerRisposta) {
        //########################################## INIZIO - INVIO LA DOMANDA ALLA IA #########################

        StringBuilder inputPrompt = creaPromptFaiSintesi(testo);
        //logger.info("DOMANDA: " + domanda);

        OpenAIGptTheokanning openAIGptTheokanning = new OpenAIGptTheokanning();
        return openAIGptTheokanning.eseguiOpenAIGptTheokanning(appConfig.getParamOpenAi().getApiKeyOpenAI(), temperature, tokensAggiuntiPerRisposta, finalcaratteriPerTokenStima,
                appConfig.getParamOpenAi().getModelGpt4_Mini(), timeoutSecondiRisposta, inputPrompt.toString() );
    }



    private StringBuilder creaPromptFaiSintesi(String testo) {
        StringBuilder domandaBuilder = new StringBuilder();
        domandaBuilder.append("Fai una sintesi ma mantieni tutti i concetti principali, senza perdere informazioni importanti: \n\n");

        // il testo a cui mettere la punteggiarura
        domandaBuilder.append(testo);

        return domandaBuilder;
    }




}




