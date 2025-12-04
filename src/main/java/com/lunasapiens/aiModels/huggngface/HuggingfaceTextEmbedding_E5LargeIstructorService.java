package com.lunasapiens.aiModels.huggngface;


import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * https://huggingface.co/settings/billing
 * https://huggingface.co/settings/tokens
 *
 * RICERCA MODELLO: https://huggingface.co/models?pipeline_tag=feature-extraction&language=it
 *
 * Questa ricerca soddisfa questi criteri:
 * feature extraction (cioè estrazione di embeddings, trasformazione del testo in vettori)
 * Supportano la lingua italiano (o sono multilingue che includono l’italiano)
 *
 *
 */
@Deprecated
@Service
public class HuggingfaceTextEmbedding_E5LargeIstructorService extends HuggingFaceBaseService {
    /**
     * Multilingual-E5-large-instruct
     * Multilingual E5 Text Embeddings: A Technical Report. Liang Wang, Nan Yang, Xiaolong Huang, Linjun Yang, Rangan Majumder, Furu Wei, arXiv 2024
     * This model has 24 layers and the embedding size is 1024.
     * Long texts will be truncated to at most 512 tokens.
     *
     *  multilingual-e5-large-instruct (dimensione dell'embedding di 1024) // TODO DA ERRORE 504 Gateway Timeout
     *  max ~400–500 parole
     *  2500–3000 caratteri
     *  per chiamata
     * https://huggingface.co/intfloat/multilingual-e5-large-instruct
     */
    private static final String MODEL_NAME = "/intfloat/multilingual-e5-large-instruct"; // DA ERRORE 504 timeoput
    private static final String MODELS = "/models";

    /**
     * Numero massimo di parole per chunk
     * sono 512 tokens ma Usare 450 token per chunk lascia un margine di sicurezza (~60 token) per evitare che l’API tronchi il testo.
     */
    private int MAX_CHUNK_WORDS = 400;


    /**
     * Metodo pubblico per generare embedding di documenti (da salvare nel DB)
     * “retrieval.passage” significa frammento di documento o passaggio di testo.
     * È pensato per rappresentare il contenuto del documento.
     * Viene ottimizzato per essere confrontato con embedding di query.
     */
    public Float[] embedDocument(String content) throws Exception {
        return embedWithChunking(content, "retrieval.passage");
    }

    /**
     * Metodo pubblico per generare embedding di query (per ricerca nel DB)
     * "retrieval.query" Rappresenta la domanda o la ricerca dell’utente.
     * È ottimizzato per confrontarsi con i passages memorizzati nel database.
     */
    public Float[] embedQuery(String content) throws Exception {
        return embedWithChunking(content, "retrieval.query");
    }


    /**
     * Gestione automatica chunking e embedding
     */
    private Float[] embedWithChunking(String content, String taskType) throws Exception {
        List<String> chunks = chunkText(content, MAX_CHUNK_WORDS);
        Float[][] embeddings = new Float[chunks.size()][];

        int index = 0;
        for (String chunk : chunks) {
            embeddings[index++] = computeEmbedding(chunk, taskType);
            Thread.sleep(2000); // 2 secondi di pausa tra le richieste
        }

        // media dei chunk
        int len = embeddings[0].length;
        Float[] avg = new Float[len];
        for (int i = 0; i < len; i++) {
            float sum = 0f;
            for (Float[] emb : embeddings) sum += emb[i];
            avg[i] = sum / embeddings.length;
        }
        return avg;
    }



    /**
     * Genera embedding normalizzato per un testo. dimensione dell'embedding di 1024
     *
     * @param content testo da trasformare in embeddinguva
     * @return Float[]
     * @throws Exception in caso di errore HTTP o parsing JSON
     */
    private Float[] computeEmbedding(String content, String taskType) throws Exception {
        Map<String, Object> payloadMap = Map.of(
                "inputs", content,
                "parameters", Map.of("task", taskType, "normalize_embeddings", true)
        );

        String payload = objectMapper.writeValueAsString(payloadMap);
        //String response = callHuggingFaceAPI( HuggingFaceBaseService.URL_HUGGING_FACE_ROOT_OLD + MODELS + MODEL_URL, payload); // OLD URL
        String response = callHuggingFaceAPI( HuggingFaceBaseService.URL_HUGGING_FACE_HF_INFERENCE_ROOT + MODELS + MODEL_NAME, payload);

        double[] embedding = objectMapper.readValue(response, double[].class);

        Float[] floatEmb = new Float[embedding.length];
        for (int i = 0; i < embedding.length; i++) floatEmb[i] = (float) embedding[i];
        return floatEmb;
    }






}
