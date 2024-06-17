package com.lunasapiens.zodiac;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.models.Choice;
import com.azure.ai.openai.models.Completions;
import com.azure.ai.openai.models.CompletionsOptions;
import com.azure.core.credential.KeyCredential;
import com.azure.core.http.HttpClient;
import com.azure.core.http.netty.NettyAsyncHttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;


/**
 * Misuratore di tokens per domanda. Copia e incolla la domanda e ti dici quanti token vengono calcoltati https://platform.openai.com/tokenizer
 *
 * gpt-4o	New GPT-4o
 * Our most advanced, multimodal flagship model that’s cheaper and faster than GPT-4 Turbo. Currently points to gpt-4o-2024-05-13.	128,000 tokens
 * PREZZO gpt-4o input: 5,00 USD/1M tokens. Output: 15,00 USD/1M tokens
 *
 * gpt-3.5-turbo-0125	The latest GPT-3.5 Turbo model with higher accuracy at responding in requested formats and a fix for a bug which caused a
 * text encoding issue for non-English language function calls. Returns a maximum of 4,096 output tokens. Learn more.	16,385 tokens
 * PREZZO gpt-3.5-turbo-0125 input: 0,50 USD/1M tokens. Output: 1,50 USD/1M tokens
 *
 * gpt-3.5-turbo-instruct Similar capabilities as GPT-3 era models. Compatible with legacy Completions endpoint and not Chat Completions.
 * PREZZO gpt-3.5-turbo-instruct input: 1,50 USD/1M tokens. Output: 2,00 USD/1M tokens
 */


public class OpenAIGptAzure {

    private static final Logger logger = LoggerFactory.getLogger(OpenAIGptAzure.class);

    /**
     * gpt-3.5-turbo-instruct non accetta ruoli come i modelli chatgpt 3.5 e 4.0 che vogliono user, system e assistant.
     * Instructor vuole solo la domanda al prompt.
     *
     * Chatgpt 3.5 e 4.0 li uso nella classe OpenAIGptTheokanning. utilizzano altre librerie.
     * ma posso anche implementarle qui con la libreria com.azure.ai.openai
     */
    public StringBuilder eseguiOpenAIGptAzure_Instruct(String apiKey, int maxTokens, double temperature, String domandaBuilder, final String modelGpt) {

        System.out.println("################### INIZIOOO eseguiOpenAiTheokanning "+ modelGpt +" ###################");

        HttpClient httpClient = new NettyAsyncHttpClientBuilder().responseTimeout(Duration.ofSeconds( 30 )).build();

        OpenAIClient client = new OpenAIClientBuilder()
                .credential(new KeyCredential(apiKey)).httpClient(httpClient).buildClient();

        List<String> prompt = new ArrayList<>();
        prompt.add(domandaBuilder.toString());
        // gpt-3.5-turbo-instruct // babbage-002 // davinci-002
        Completions completions = client.getCompletions( modelGpt, new CompletionsOptions( prompt ).setMaxTokens( maxTokens ).setTemperature( temperature ));

        logger.info("temperature: " + temperature + " setMaxTokens: " + maxTokens + " Model ID:" + completions.getId() + " is created at: " + completions.getCreatedAt());

        StringBuilder risposta = new StringBuilder();
        for (Choice choice : completions.getChoices()) {
            risposta.append(choice.getText());
            System.out.printf("Index: %d, Text: %s.\\n", choice.getIndex(), choice.getText());
            //logger.info( "choice.getText(): "+choice.getText() );
        }

        return risposta;
    }
}

