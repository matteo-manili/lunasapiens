package com.lunasapiens.manualjobs.SpeechToChunks;

import com.lunasapiens.entity.Chunks;
import com.lunasapiens.manualjobs.SpeechToChunks.service.RAGIAService;
import com.lunasapiens.service.ChunksService;
import com.lunasapiens.service.TextEmbeddingService;
import com.theokanning.openai.completion.chat.ChatMessage;
import org.json.JSONObject;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class RAGManualJob {


    @Autowired
    TextEmbeddingService textEmbeddingService;

    @Autowired
    ChunksService chunksService;

    @Autowired
    RAGIAService RAGIAService;


    @Test
    //@Disabled("Disabilitato temporaneamente per debug")
    public void OperazioniRAG() throws Exception {
        String userInput = "Come faccio a non farmi manipolare dagli altri?";

        List<Chunks> listCunks = chunksService.findNearestChunksWithFts(userInput, 5); //10
        StringBuilder context = new StringBuilder();
        System.out.println("========================================================");
        for(Chunks chunk : listCunks ) {
            System.out.println("ID Video "+chunk.getVideoChunks().getNumeroVideo() );

            // Estrai summary dal JSON dei metadati
            String metadati = chunk.getVideoChunks().getMetadati();
            if(metadati != null && !metadati.isEmpty()) {
                JSONObject json = new JSONObject(metadati);
                if(json.has("summary")) {
                    String summary = json.getString("summary");
                    System.out.println("Summary: " + summary);
                }
            }
            System.out.println("Numero Chunk:" +chunk.getChunkIndex() +" content: "+  chunk.getContent() );
            System.out.println("========================================================");
            context.append(chunk.getContent()).append("\n");
        }

        // 4. Prepara i messaggi per la chat
        List<ChatMessage> messages = new ArrayList<>();

        messages.add(new ChatMessage("system",
                "Sei uno psicologo empatico e professionale. Rispondi solo usando il contesto fornito e non aggiungere consigli esterni."));
        messages.add(new ChatMessage("user","Contesto: " + context.toString() +
                        "\n\nDomanda: " + userInput));

        RAGIAService.chiediAlloPsicologo( messages, 0.2, 1000 );
    }







}
