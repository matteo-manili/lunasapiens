package com.lunasapiens.service.aiModels.huggngface;

import org.springframework.stereotype.Service;
import java.util.*;

/**
 * info modello https://huggingface.co/swap-uniba/LLaMAntino-3-ANITA-8B-Inst-DPO-ITA
 */
@Service
public class HuggingfaceLLaMASummarizationService extends HuggingFaceBaseService {


    protected static final String URL_HUGGING_FACE_CHAT_COMPLETIONS = "/v1/chat/completions";
    private static final String MODEL_NAME = "swap-uniba/LLaMAntino-3-ANITA-8B-Inst-DPO-ITA";

    private int MAX_CHUNK_WORDS = 5700; // Sono 8.000 token corrispondono grosso modo a 5.500–6.000 parole in italiano o inglese


    public String summarize(String content, boolean asTitle) throws Exception {
        List<String> chunks = chunkText(content, MAX_CHUNK_WORDS);
        List<String> partialSummaries = new ArrayList<>();

        for (String chunk : chunks) {
            partialSummaries.add(generateSummary(chunk, 120, asTitle));
        }

        String combined = String.join(" ", partialSummaries);

        // seconda sintesi più breve → titolo o riassunto compatto
        int maxLen = asTitle ? 60 : 150;
        return generateSummary(combined, maxLen, asTitle);
    }

    private String generateSummary(String text, int maxNewTokens, boolean asTitle) throws Exception {
        String userPrompt = asTitle
                ? "Genera un titolo sintetico in italiano per il seguente testo:\n\n" + text
                : "Fai un riassunto conciso in italiano del seguente testo:\n\n" + text;

        // Costruisci lo schema "messages" in stile OpenAI
        List<Map<String, String>> messages = List.of(
                Map.of("role", "system", "content", "Sei un assistente AI esperto in sintesi e titoli."),
                Map.of("role", "user", "content", userPrompt)
        );

        Map<String, Object> payloadMap = Map.of(
                "model", MODEL_NAME + ":featherless-ai",
                "messages", messages,
                "max_tokens", maxNewTokens,
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
    }


}
