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

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public abstract class HuggingFaceBaseService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    protected HuggingFaceConfig huggingFaceConfig;

    protected static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * To maintain service continuity, please migrate to the new Inference Providers API. It can be as seamless
     * as replacing https://api-inference.huggingface.co/ in your calls with https://router.huggingface.co/hf-inference/.
     */
    // API root
    protected static final String URL_HUGGING_FACE_ROOT = "https://router.huggingface.co";

    protected static final String URL_HUGGING_FACE_EMBEDDINGS = "/hf-inference/models";
    protected static final String URL_HUGGING_FACE_CHAT_COMPLETIONS = "/v1/chat/completions";


    /**
     * Divide un testo lungo in blocchi più piccoli.
     */
    protected List<String> chunkText(String content, int maxWords) {
        String[] words = content.split("\\s+");
        List<String> chunks = new ArrayList<>();

        for (int i = 0; i < words.length; i += maxWords) {
            int end = Math.min(i + maxWords, words.length);
            chunks.add(String.join(" ", Arrays.copyOfRange(words, i, end)));
        }

        return chunks;
    }

    /**
     * Metodo generico per chiamare un modello Hugging Face.
     *
     * @param url URL del modello (es. "ARTeLab/mbart-summarization-mlsum")
     * @param payload  JSON serializzato con inputs e parameters
     * @return stringa JSON della risposta
     */
    protected String callHuggingFaceAPI(String url, String payload) throws Exception {
        // Se l'URL inizia con http, lo usa così com'è, altrimenti lo concatena al root
        String finalUrl = url.startsWith("http") ? url : URL_HUGGING_FACE_ROOT + URL_HUGGING_FACE_EMBEDDINGS + url;

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(finalUrl);
            post.addHeader("Authorization", "Bearer " + huggingFaceConfig.getToken());
            post.setHeader("Accept", "application/json");
            post.setEntity(new StringEntity(payload, ContentType.APPLICATION_JSON));

            try (InputStream responseStream = client.execute(post).getEntity().getContent()) {
                String responseBody = new String(responseStream.readAllBytes(), StandardCharsets.UTF_8);

                if (responseBody.contains("error") || responseBody.contains("Not Found")) {
                    throw new RuntimeException("HuggingFace API error: " + responseBody);
                }

                return responseBody;
            }
        }
    }

}
