package com.lunasapiens.zodiac;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.models.*;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.credential.KeyCredential;
import com.azure.core.exception.HttpResponseException;
import com.azure.core.models.ResponseError;
import com.lunasapiens.Constants;
import com.lunasapiens.TikTokApiClient;
import com.lunasapiens.dto.GiornoOraPosizioneDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


public class ServiziAstrologici {

    private static final Logger logger = LoggerFactory.getLogger(ServiziAstrologici.class);

    private String keyOpenAi;
    private Double temperature = 1.0;
    private Integer maxTokens = 1000;


    public ServiziAstrologici(String keyOpenAi) {
        this.keyOpenAi = keyOpenAi;
    }

    public StringBuilder servizioOroscopoDelGiorno(String segno, GiornoOraPosizioneDTO giornoOraPosizioneDTO) {
        return servizioOroscopoDelGiorno(temperature, maxTokens, segno, giornoOraPosizioneDTO);
    }

    public StringBuilder servizioOroscopoDelGiorno(Double temperature, Integer maxTokens, String segno, GiornoOraPosizioneDTO giornoOraPosizioneDTO) {

        BuildInfoAstrologia buildInfoAstrologia = new BuildInfoAstrologia( giornoOraPosizioneDTO );

        System.out.println("############################ TEXT IA ###################################");
        System.out.println("############################ TEXT IA ###################################");
        String domanda = "Crea l'oroscopo del giorno (di massimo 200 parole) per il segno del "+ segno +" in base a questi dati. \n" +
                "il testo generato deve essere diviso in blocchi di 30-40 parole e tra un blocco e l'altro devi inserie il carattere speciale "+Constants.SeparatoreTestoOroscopo+". \n"+
                "Il giorno di oggi è: "+giornoOraPosizioneDTO.getGiorno()+ "/" +giornoOraPosizioneDTO.getMese()+ "/" +giornoOraPosizioneDTO.getAnno()
                + " ore "+giornoOraPosizioneDTO.getOra()+":"+giornoOraPosizioneDTO.getMinuti()+ "\n"+
                "Transiti di oggi: " + "\n";

        for(PianetiAspetti var : buildInfoAstrologia.getPianetiAspetti()){
            if ( var.getNomePianeta().equals(Constants.NAME_PLANET[0]) ||
                    var.getNomePianeta().equals(Constants.NAME_PLANET[1]) ||
                    var.getNomePianeta().equals(Constants.NAME_PLANET[2]) ||
                    var.getNomePianeta().equals(Constants.NAME_PLANET[3]) ||
                    var.getNomePianeta().equals(Constants.NAME_PLANET[4]) ){
                domanda += var.toString();
                //System.out.println( var.toString() );
            }
        }

        domanda += "\n" + "Case Placide di oggi: " + "\n";
        for(CasePlacide var : buildInfoAstrologia.getCasePlacide()){
            domanda += var.toString();
            //System.out.println( var.toString() );
        }

        logger.info("DOMANDA: "+ domanda );


        // @@@@@@@@@@@@@@@@  @@@@@@@@@@@@@@@@@@@@@@

        OpenAIClient client = new OpenAIClientBuilder().credential(new KeyCredential( keyOpenAi )).buildClient();
        List<String> prompt = new ArrayList<>();
        prompt.add( domanda );
        // gpt-3.5-turbo-instruct // babbage-002 // davinci-002
        Completions completions = client.getCompletions("gpt-3.5-turbo-instruct",
                new CompletionsOptions(prompt).setMaxTokens( maxTokens ).setTemperature( temperature ));

        logger.info("Model ID=%s is created at %s.%n", completions.getId(), completions.getCreatedAt());
        logger.info("setMaxTokens: "+temperature +" setMaxTokens: "+maxTokens);

        StringBuilder risposta = new StringBuilder();
        for (Choice choice : completions.getChoices()) {
            System.out.printf("Index: %d, Text: %s.%n", choice.getIndex(), choice.getText());
            risposta.append(choice.getText()).append("\n");
        }



  // ######################################################## IMMAGINE OPENAI - OPENAI Azure ########################################################
        // esempio preso da https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/openai/azure-ai-openai/src/samples/java/com/azure/ai/openai/usage/GetImagesSample.java
        /*
        try {
            //String azureOpenaiKey = "{azure-open-ai-key}";
            //String endpoint = "{azure-open-ai-endpoint}";

            //L'idea è di mettere il testo generato dell'orscoppo giornaliero come parametro per generare l'immaginr IA

            OpenAIClient client = new OpenAIClientBuilder().credential(new AzureKeyCredential( this.keyOpenAi )).
                    buildClient();

            ImageGenerationOptions imageGenerationOptions = new ImageGenerationOptions(
                    "segno zodiacale acquario gioioso");

            imageGenerationOptions.setSize(ImageSize.fromString("256x256")) ;
            ImageResponse images = client.getImages(imageGenerationOptions);

            for (ImageLocation imageLocation : images.getData()) {
                ResponseError error = imageLocation.getError();
                if (error != null) {
                    System.out.printf("Image generation operation failed. Error code: %s, error message: %s.%n",
                            error.getCode(), error.getMessage());
                } else {
                    System.out.printf(
                            "Image location URL that provides temporary access to download the generated image is %s.%n",
                            imageLocation.getUrl());
                    String urlImege = imageLocation.getUrl();
                    //model.addAttribute("oroscopoGpt", urlImege  );
                    System.out.println("urlImege: "+urlImege);
                }
            }
        } catch (HttpResponseException hre){
            //model.addAttribute("oroscopoGpt", hre.getValue() +" "+ hre.getMessage()  );
        }

        */
        // ####################### FINE IMMAGINE OPENAI ##################


        StringBuilder aa = new StringBuilder("");
        return risposta; //risposta;
    }


}


/*
        // ######################################################## IMMAGINE OPENAI ########################################################
        // esempio preso da https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/openai/azure-ai-openai/src/samples/java/com/azure/ai/openai/usage/GetImagesSample.java
        try {

            //String azureOpenaiKey = "{azure-open-ai-key}";
            //String endpoint = "{azure-open-ai-endpoint}";

            OpenAIClient client = new OpenAIClientBuilder().credential(new AzureKeyCredential(apiKey)).buildClient();

            ImageGenerationOptions imageGenerationOptions = new ImageGenerationOptions(
                    "Crea 3 immagini di donne avvenenti in stile Van Gogh");

            ImageResponse images = client.getImages(imageGenerationOptions);

            for (ImageLocation imageLocation : images.getData()) {
                ResponseError error = imageLocation.getError();
                if (error != null) {
                    System.out.printf("Image generation operation failed. Error code: %s, error message: %s.%n",
                            error.getCode(), error.getMessage());
                } else {
                    System.out.printf(
                            "Image location URL that provides temporary access to download the generated image is %s.%n",
                            imageLocation.getUrl());
                    String urlImege = imageLocation.getUrl();
                    model.addAttribute("oroscopoGpt", urlImege  );
                    System.out.println("urlImege: "+urlImege);
                }
            }
        } catch (HttpResponseException hre){
            model.addAttribute("oroscopoGpt", hre.getValue() +" "+ hre.getMessage()  );
        }

*/