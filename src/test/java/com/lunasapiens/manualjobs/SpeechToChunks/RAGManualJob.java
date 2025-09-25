package com.lunasapiens.manualjobs.SpeechToChunks;

import com.lunasapiens.entity.Chunks;
import com.lunasapiens.manualjobs.SpeechToChunks.service.RAGIAService;
import com.lunasapiens.repository.ChunksCustomRepositoryImpl;
import com.lunasapiens.service.ChunksService;
import com.lunasapiens.service.TextEmbeddingService;
import com.theokanning.openai.completion.chat.ChatMessage;
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
    private ChunksCustomRepositoryImpl chunksCustomRepository;

    @Autowired
    ChunksService chunksService;

    @Autowired
    RAGIAService RAGIAService;


    @Test
    //@Disabled("Disabilitato temporaneamente per debug")
    public void OperazioniRAG() throws Exception {
        String userInput = "è giusto perdonare i miei genitori se io non li amo?";

        //List<Chunks> listCunks = chunksService.findNearestChunks(userInput, 5);
        List<Chunks> listCunks = chunksService.findNearestChunksWithFts(userInput, 10);

        StringBuilder context = new StringBuilder();
        for(Chunks chunk : listCunks ) {
            System.out.println("ID Video "+chunk.getVideoId() );
            System.out.println("Numero Chunk:" +chunk.getChunkIndex() +" content: "+  chunk.getContent() );
            context.append(chunk.getContent()).append("\n");
        }

        // 4. Prepara i messaggi per la chat
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage("system",
                "Sei uno psicologo empatico e professionale. " +
                        "Rispondi solo basandoti sul contesto fornito, ignora altre informazioni esterne. " +
                        "Mantieni un tono comprensivo e non aggiungere consigli esterni. "
        ));

        messages.add(new ChatMessage("system",
                "Contesto:\n" + context.toString()
        ));

        messages.add(new ChatMessage("user", "Domanda: " + userInput));




        RAGIAService.chiediAlloPsicologo( messages );


    }


}
