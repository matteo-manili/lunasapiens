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
 * questo funziona bene: multilingual-e5-large-instruct (dimensione dell'embedding di 1024)
 */
@Service
public class TextEmbeddingHuggingfaceService {

    private static final Logger logger = LoggerFactory.getLogger(TextEmbeddingHuggingfaceService.class);

    @Autowired
    private HuggingFaceConfig huggingFaceConfig;

    /**
     * Per mantenere la continuità del servizio, migra alla nuova API Inference Providers.
     * È sufficiente sostituire https://api-inference.huggingface.co/ nelle chiamate con https://router.huggingface.co/hf-inference/ .
     */
    private static final String HF_MODEL_URL = "https://router.huggingface.co/hf-inference/models/intfloat/multilingual-e5-large-instruct"; // dimensione dell'embedding di 1024


    private static final ObjectMapper objectMapper = new ObjectMapper();


    /**
     * Genera embedding normalizzato per un testo. dimensione dell'embedding di 1024
     *
     * @param content testo da trasformare in embeddinguva
     * @return Float[]
     * @throws Exception in caso di errore HTTP o parsing JSON
     */
    public Float[] computeCleanEmbedding(String content) throws Exception {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(HF_MODEL_URL);
            post.addHeader("Authorization", "Bearer " + huggingFaceConfig.getToken());
            post.setHeader("Accept", "application/json");

            // Payload corretto per feature-extraction
            Map<String, String> payloadMap = Map.of("inputs", content);
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
