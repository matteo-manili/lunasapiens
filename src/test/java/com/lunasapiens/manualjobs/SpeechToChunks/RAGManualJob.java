package com.lunasapiens.manualjobs.SpeechToChunks;

import com.lunasapiens.entity.Chunks;
import com.lunasapiens.service.RAGIAService;
import com.lunasapiens.service.ChunksService;
import com.lunasapiens.service.TextEmbeddingHuggingfaceService;
import com.lunasapiens.service.TextEmbeddingService;
import com.theokanning.openai.completion.chat.ChatMessage;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringBootTest
class RAGManualJob {


    @Autowired
    TextEmbeddingService textEmbeddingService;

    @Autowired
    TextEmbeddingHuggingfaceService textEmbeddingHuggingfaceService;

    @Autowired
    ChunksService chunksService;

    @Autowired
    RAGIAService rAGIAService;



    @Test
    @Disabled("Disabilitato temporaneamente per debug")
    public void eseguiEmbeddingHuggingface() throws Exception {

        textEmbeddingHuggingfaceService.computeCleanEmbedding("ciao bellooo");
    }




    @Test
    @Disabled("Disabilitato temporaneamente per debug")
    public void OperazioniRAG() throws Exception {
        String userInput = "parlami della masturbazione. la pratico molto, da che dipende?";

        List<Chunks> listCunks = chunksService.findNearestChunksCosine(userInput, 5); //10
        StringBuilder context = new StringBuilder();
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
            context.append(chunk.getContent()).append("\n");
        }

        // 4. Prepara i messaggi per la chat
        List<ChatMessage> messages = new ArrayList<>();

        messages.add(new ChatMessage("system", "Sei uno psicologo empatico e professionale. Rispondi solo usando il contesto fornito e non aggiungere consigli esterni."));
        messages.add(new ChatMessage("user","Contesto: " + context.toString() + "\n\nDomanda: " + userInput));

        rAGIAService.chiediAlloPsicologo( messages, 0.0, 1000 );
    }







}
