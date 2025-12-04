package com.lunasapiens.aiModels.huggngface;

import com.lunasapiens.utils.UtilsArticleSeo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * info modello https://huggingface.co/swap-uniba/LLaMAntino-3-ANITA-8B-Inst-DPO-ITA
 *
 * Context length: 8K, 8192. Token
 */
@Service
public class HuggingfaceLLaMAGenerateSEOTextArticleService extends HuggingFaceBaseService {


    protected static final String URL_HUGGING_FACE_CHAT_COMPLETIONS = "/v1/chat/completions";
    private static final String MODEL_NAME = "swap-uniba/LLaMAntino-3-ANITA-8B-Inst-DPO-ITA";


    public String generateTitle(String content, Long articleId) {

        String text = UtilsArticleSeo.cleanText(content);
        System.out.println("text pulito: " + text);

        String textSystem = "Genera un titolo di max 10 parole per il seguente testo:\n\n" + text;

        int numTokenInput = countTokens(MODEL_NAME, textSystem);
        int numTokenOutput = estimateTokensFromWords(10);


        int totalTokens = numTokenInput + numTokenOutput;

        System.out.println("totalTokens: " + totalTokens);

        String title = eseguiLLaM(textSystem, "", totalTokens);



        // fallback se la generazione fallisce
        if (title == null || title.isBlank()) {
            title = "Articolo di approfondimento " + articleId;
        }

        return title;
    }





    private String eseguiLLaM(String textSystem, String textUser, int numToken) {
        try {

            // Costruisci lo schema "messages" in stile OpenAI
            List<Map<String, String>> messages = List.of(
                    Map.of("role", "system", "content", textSystem),
                    Map.of("role", "user", "content", textUser)
            );

            Map<String, Object> payloadMap = Map.of(
                    "model", MODEL_NAME + ":featherless-ai",
                    "messages", messages,
                    "max_tokens", numToken,
                    "temperature", 0.2,
                    "top_p", 0.9
            );


            String payload = objectMapper.writeValueAsString(payloadMap);

            // ✅ endpoint corretto: niente MODEL_URL nel path!
            String response = callHuggingFaceAPI(
                    HuggingFaceBaseService.URL_HUGGING_FACE_ROOT + URL_HUGGING_FACE_CHAT_COMPLETIONS, // endpoint corretto
                    payload
            );


            // parsing OpenAI-style JSON response
            Map<String, Object> result = objectMapper.readValue(response, Map.class);
            List<Map<String, Object>> choices = (List<Map<String, Object>>) result.get("choices");
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");

            return (String) message.get("content");

        } catch (Exception e) {
            logger.error("Errore durante l'esecuzione di LLaMA: {}", e.getMessage(), e);
            // fallback in caso di errore
            return null;
        }
    }


}
