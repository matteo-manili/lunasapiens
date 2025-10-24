package com.lunasapiens.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lunasapiens.config.HuggingFaceConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
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
@Service
public class TextEmbeddingHuggingfaceService {

    private static final Logger logger = LoggerFactory.getLogger(TextEmbeddingHuggingfaceService.class);

    @Autowired
    private HuggingFaceConfig huggingFaceConfig;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final String HF_URL_API = "https://router.huggingface.co/hf-inference/";

    /**
     * Multilingual-E5-large-instruct
     * Multilingual E5 Text Embeddings: A Technical Report. Liang Wang, Nan Yang, Xiaolong Huang, Linjun Yang, Rangan Majumder, Furu Wei, arXiv 2024
     * This model has 24 layers and the embedding size is 1024.
     * Long texts will be truncated to at most 512 tokens.
     *
     *  questo funziona bene: multilingual-e5-large-instruct (dimensione dell'embedding di 1024)
     *  max ~400–500 parole
     *  2500–3000 caratteri
     *  per chiamata
     *
     * https://huggingface.co/intfloat/multilingual-e5-large-instruct
     */
    private static final String HF_URL_MODEL_MULTI_E5_LG_INSTRUCT = HF_URL_API + "models/intfloat/multilingual-e5-large-instruct"; // dimensione dell'embedding di 1024

    /**
     * Numero massimo di parole per chunk
     * Usare 450 token per chunk lascia un margine di sicurezza (~60 token) per evitare che l’API tronchi il testo.
     */
    private static final int MAX_CHUNK_LENGTH = 450;






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
        String[] words = content.split("\\s+");
        int totalWords = words.length;

        logger.info("numero parole: "+totalWords);

        // Lista per raccogliere gli embedding dei chunk
        Float[][] chunkEmbeddings = new Float[(totalWords + MAX_CHUNK_LENGTH - 1) / MAX_CHUNK_LENGTH][];

        int chunkIndex = 0;
        for (int start = 0; start < totalWords; start += MAX_CHUNK_LENGTH) {
            int end = Math.min(start + MAX_CHUNK_LENGTH, totalWords);
            String chunkText = String.join(" ", java.util.Arrays.copyOfRange(words, start, end));
            chunkEmbeddings[chunkIndex++] = computeCleanEmbedding(chunkText, taskType);
        }

        // Media dei chunk embedding
        int embeddingLength = chunkEmbeddings[0].length;
        Float[] finalEmbedding = new Float[embeddingLength];
        for (int i = 0; i < embeddingLength; i++) {
            float sum = 0f;
            for (Float[] chunk : chunkEmbeddings) {
                sum += chunk[i];
            }
            finalEmbedding[i] = sum / chunkEmbeddings.length;
        }

        return finalEmbedding;
    }



    /**
     * Genera embedding normalizzato per un testo. dimensione dell'embedding di 1024
     *
     * @param content testo da trasformare in embeddinguva
     * @return Float[]
     * @throws Exception in caso di errore HTTP o parsing JSON
     */
    private Float[] computeCleanEmbedding(String content, String taskType) throws Exception {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(HF_URL_MODEL_MULTI_E5_LG_INSTRUCT);
            post.addHeader("Authorization", "Bearer " + huggingFaceConfig.getToken());
            post.setHeader("Accept", "application/json");
            Map<String, Object> payloadMap = Map.of(
                    "inputs", content,
                    "parameters", Map.of(
                            "task", taskType,
                            "normalize_embeddings", true
                    )
            );
            String payload = objectMapper.writeValueAsString(payloadMap);
            post.setEntity(new StringEntity(payload, ContentType.APPLICATION_JSON));
            try (InputStream responseStream = client.execute(post).getEntity().getContent()) {
                String responseBody = new String(responseStream.readAllBytes());
                //logger.info("Hugging Face response: " + responseBody);

                // Controllo errori
                if (responseBody.contains("error")) {
                    throw new RuntimeException("HuggingFace API error: " + responseBody);
                }

                // La risposta è un array singolo di float
                double[] embedding = objectMapper.readValue(responseBody, double[].class);

                // Conversione in Float[]
                Float[] floatEmbedding = new Float[embedding.length];
                for (int i = 0; i < embedding.length; i++) {
                    floatEmbedding[i] = (float) embedding[i];
                }

                return floatEmbedding;
            }
        }
    }






}
