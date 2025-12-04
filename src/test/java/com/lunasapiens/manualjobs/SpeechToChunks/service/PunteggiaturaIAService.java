package com.lunasapiens.manualjobs.SpeechToChunks.service;

import com.lunasapiens.service.aiModels.openai.OpenAIGptTheokanning;
import com.lunasapiens.config.AppConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class PunteggiaturaIAService {

    private static final Logger logger = LoggerFactory.getLogger(PunteggiaturaIAService.class);

    @Autowired
    private AppConfig appConfig;


    // tokensRisposta signfiica i token da aggiungere oltre i token per la domanda
    private Double temperature = 0.2;
    private final Double finalcaratteriPerTokenStima = 10.0;
    private Integer timeoutSecondiRisposta = 500; // secondi attesa riposta


    /**
     * i tokensAggiuntiPerRisposta corrispondno al numero di parole del testo. in questo modo la risposta della LLM è completa.
     * cioè mette la punteggiatura a tutto il testo senza troncare il testo.
     */
    public StringBuilder generaTestoConPunteggiatura(String testo, Integer tokensAggiuntiPerRisposta) {
        //########################################## INIZIO - INVIO LA DOMANDA ALLA IA #########################

        StringBuilder inputPrompt = creaPromptPunteggiatura(testo);
        //logger.info("DOMANDA: " + domanda);

        OpenAIGptTheokanning openAIGptTheokanning = new OpenAIGptTheokanning();
        return openAIGptTheokanning.eseguiOpenAIGptTheokanning(appConfig.getParamOpenAi().getApiKeyOpenAI(), temperature, tokensAggiuntiPerRisposta, finalcaratteriPerTokenStima,
                appConfig.getParamOpenAi().getModelGpt4_Mini(), timeoutSecondiRisposta, inputPrompt.toString() );
    }



    private StringBuilder creaPromptPunteggiatura(String testo) {
        StringBuilder domandaBuilder = new StringBuilder();
        domandaBuilder.append("inserisci la punteggiatura a questo testo senza togliere e modificare le parole: \n\n");

        // il testo a cui mettere la punteggiarura
        domandaBuilder.append(testo);

        return domandaBuilder;
    }




}




