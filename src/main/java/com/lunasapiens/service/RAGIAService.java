package com.lunasapiens.service;

import com.lunasapiens.config.AppConfig;
import com.lunasapiens.entity.Chunks;
import com.lunasapiens.aiModels.openai.OpenAIGptTheokanning;
import com.theokanning.openai.completion.chat.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


@Component
public class RAGIAService {

    private static final Logger logger = LoggerFactory.getLogger(RAGIAService.class);

    @Autowired
    private AppConfig appConfig;

    @Autowired
    ChunksService chunksService;



    public static StringBuilder psicologoIstruzioneBOTSystem(){
        StringBuilder textSystemBuilder = new StringBuilder();
        textSystemBuilder.append("Sei uno psicologo empatico e professionale. Rispondi usando esclusivamente le Informazioni fornite e non consigliare mai di rivolgersi ad un professionista.");
        return textSystemBuilder;
    }


    public StringBuilder getChunksContext(String userInput, int limit){
        List<Chunks> listCunks = chunksService.findNearestChunksCosine(userInput, limit);
        StringBuilder contextChunks = new StringBuilder();
        System.out.println("========================================================");
        for(Chunks chunk : listCunks ) {
            System.out.println("ID Video "+chunk.getVideoChunks().getNumeroVideo() + " TITOLO VIDEO: "+chunk.getVideoChunks().getTitle());

            // Estrai summary dal JSON dei metadati
            Map<String, Object> metadati = chunk.getVideoChunks().getMetadati();
            if (metadati != null && !metadati.isEmpty()) {
                Object summaryObj = metadati.get("summary");
                if (summaryObj instanceof String) {
                    String summary = (String) summaryObj;
                    if (!summary.isBlank()) {
                        System.out.println("Summary: " + summary);
                    }
                }
            }

            System.out.println("Numero Chunk:" +chunk.getChunkIndex() +" content: "+  chunk.getContent() );
            System.out.println("========================================================");
            contextChunks.append(chunk.getContent()).append("\n");
        }
        return contextChunks;

    }






    // tokensRisposta signfiica i token da aggiungere oltre i token per la domanda
    private Double temperature = 0.2;
    private final Double finalcaratteriPerTokenStima = 10.0;
    private Integer tokensAggiuntiPerRisposta = 1500;


    /**
     * i tokensAggiuntiPerRisposta corrispondno al numero di parole del testo. in questo modo la risposta della LLM è completa.
     * cioè mette la punteggiatura a tutto il testo senza troncare il testo.
     */
    public StringBuilder chiediAlloPsicologo(List<ChatMessage> chatMessageList, Double temperature, Integer tokensAggiuntiPerRisposta) {
        //########################################## INIZIO - INVIO LA DOMANDA ALLA IA #########################

        OpenAIGptTheokanning openAIGptTheokanning = new OpenAIGptTheokanning();

        return openAIGptTheokanning.eseguiOpenAIGptTheokanning(appConfig.getParamOpenAi().getApiKeyOpenAI(), temperature, tokensAggiuntiPerRisposta, finalcaratteriPerTokenStima,
                appConfig.getParamOpenAi().getModelGpt4_Mini(), chatMessageList );

    }







}




