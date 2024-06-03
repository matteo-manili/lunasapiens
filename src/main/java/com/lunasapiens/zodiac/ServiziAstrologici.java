package com.lunasapiens.zodiac;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.models.*;
import com.azure.core.credential.KeyCredential;
import com.lunasapiens.Constants;
import com.lunasapiens.dto.GiornoOraPosizioneDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ServiziAstrologici {

    private static final Logger logger = LoggerFactory.getLogger(ServiziAstrologici.class);

    private static String keyOpenAi;
    private Double temperature = 1.0; private Integer maxTokens = 800;


    public ServiziAstrologici(String keyOpenAi) {
        this.keyOpenAi = keyOpenAi;
    }

    public StringBuilder oroscopoDelGiornoIA(String segno, GiornoOraPosizioneDTO giornoOraPosizioneDTO) {
        return oroscopoDelGiorno(temperature, maxTokens, segno, giornoOraPosizioneDTO);
    }


    public static String oroscopoDelGiornoDescrizioneOggi(GiornoOraPosizioneDTO giornoOraPosizioneDTO) {

        BuildInfoAstrologiaSwiss buildInfoAstroSwiss = new BuildInfoAstrologiaSwiss();

        String descrizioneOggi = "Oggi è: " + giornoOraPosizioneDTO.getGiorno() + "/" + giornoOraPosizioneDTO.getMese() + "/" + giornoOraPosizioneDTO.getAnno()
                + " ore " + giornoOraPosizioneDTO.getOra() + ":" + giornoOraPosizioneDTO.getMinuti() + "\n" +
                "Transiti di oggi: " + "\n";

        ArrayList<PianetaPosizione> pianetiTransiti = buildInfoAstroSwiss.getPianetiTransiti(giornoOraPosizioneDTO);

        for (PianetaPosizione var : pianetiTransiti) {
            if (var.getNomePianeta().equals(Constants.NAME_ITA_PLANET[0]) ||
                    var.getNomePianeta().equals(Constants.NAME_ITA_PLANET[1]) ||
                    var.getNomePianeta().equals(Constants.NAME_ITA_PLANET[2]) ||
                    var.getNomePianeta().equals(Constants.NAME_ITA_PLANET[3]) ||
                    var.getNomePianeta().equals(Constants.NAME_ITA_PLANET[4])) {
                descrizioneOggi += var.descrizionePianeta();
                //System.out.println( var.toString() );
            }
        }


        PianetiAspetti pianetiAspetti = new PianetiAspetti();
        ArrayList<String> aspetti = pianetiAspetti.verificaAspetti(pianetiTransiti);
        if(aspetti.size() > 0) {
            descrizioneOggi += "\n" + "Aspetti: " + "\n";
            for(String var : aspetti){
                descrizioneOggi += var + ". ";
            }
        }

        descrizioneOggi += "\n" + "Case Placide di oggi: " + "\n";
        for (CasePlacide var : buildInfoAstroSwiss.getCasePlacide(giornoOraPosizioneDTO)) {
            descrizioneOggi += var.descrizioneCasa();
        }

        return descrizioneOggi;
    }


    public static StringBuilder oroscopoDelGiorno(Double temperature, Integer maxTokens, String segno, GiornoOraPosizioneDTO giornoOraPosizioneDTO) {

        BuildInfoAstrologiaSwiss buildInfoAstroSwiss = new BuildInfoAstrologiaSwiss();

        System.out.println("############################ TEXT IA ###################################");

        /*
        String domanda = "Crea l'oroscopo del giorno (di massimo 200 parole) per il segno del "+ segno +" in base a questi dati. \n" +
                "il testo generato deve essere diviso in blocchi sensati di 30-40 parole e ogni blocco deve terminare con il carattere speciale "+Constants.SeparatoreTestoOroscopo+"\n"+
                "Oggi è: "+giornoOraPosizioneDTO.getGiorno()+ "/" +giornoOraPosizioneDTO.getMese()+ "/" +giornoOraPosizioneDTO.getAnno()
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
         */

        StringBuilder domandaBuilder = new StringBuilder();
        domandaBuilder.append("Crea l'oroscopo del giorno (di massimo 300 parole) per il segno del ").append(segno).append(". ").append("\n")
                .append("Oggi è: ").append(giornoOraPosizioneDTO.getGiorno()).append("/").append(giornoOraPosizioneDTO.getMese()).append("/")
                .append(giornoOraPosizioneDTO.getAnno()).append(" ore ").append(giornoOraPosizioneDTO.getOra()).append(":")
                .append(giornoOraPosizioneDTO.getMinuti()).append("\n").append("Transiti: ");

        ArrayList<PianetaPosizione> pianetiTransiti = buildInfoAstroSwiss.getPianetiTransiti(giornoOraPosizioneDTO);
        for (PianetaPosizione var : pianetiTransiti) {
            if (Arrays.asList(Constants.NAME_ITA_PLANET).contains(var.getNomePianeta())) {
                domandaBuilder.append(var.descrizionePianeta());
                //System.out.println( var.toString() );
            }
        }

        PianetiAspetti pianetiAspetti = new PianetiAspetti();
        ArrayList<String> aspetti = pianetiAspetti.verificaAspetti(pianetiTransiti);
        if(aspetti.size() > 0) {
            domandaBuilder.append("\n").append("Aspetti: ");
            for(String var : aspetti) {
                domandaBuilder.append(var + ". ");
            }
        }

        domandaBuilder.append("\n").append("Case Placide: ");
        for (CasePlacide var : buildInfoAstroSwiss.getCasePlacide(giornoOraPosizioneDTO)) {
            domandaBuilder.append(var.descrizioneCasa());
            //System.out.println( var.toString() );
        }

        logger.info("DOMANDA: " + domandaBuilder.toString());

        // @@@@@@@@@@@@@@@@ INVIO LA DOMANDA ALLA IA @@@@@@@@@@@@@@@@@@@@@@

        OpenAIClient client = new OpenAIClientBuilder().credential(new KeyCredential(keyOpenAi)).buildClient();
        List<String> prompt = new ArrayList<>();
        prompt.add(domandaBuilder.toString());
        // gpt-3.5-turbo-instruct // babbage-002 // davinci-002
        Completions completions = client.getCompletions("gpt-3.5-turbo-instruct",
                new CompletionsOptions(prompt).setMaxTokens(maxTokens).setTemperature(temperature));

        logger.info("temperature: " + temperature + " setMaxTokens: " + maxTokens + " Model ID:" + completions.getId() + " is created at: " + completions.getCreatedAt());

        StringBuilder risposta = new StringBuilder();
        for (Choice choice : completions.getChoices()) {
            risposta.append(choice.getText());
            System.out.printf("Index: %d, Text: %s.\\n", choice.getIndex(), choice.getText());
            //logger.info( "choice.getText(): "+choice.getText() );
        }

        return risposta;

    }


    public static ArrayList<String> dividiParagrafiStringBuilderIA(StringBuilder risposta) {
        // divido faccio lo split in base ai paragrafi, che vengono segnati nel testo con il carattere \n
        String[] rispostaSplitParagrafi = risposta.toString().split("\n");
        ArrayList<String> paragrafiList = new ArrayList<>();
        logger.info("rispostaSplitParagrafi.length: " + rispostaSplitParagrafi.length);
        for (String paragrafo : rispostaSplitParagrafi) {
            if (!paragrafo.trim().isEmpty()) {
                paragrafiList.add(paragrafo.trim());
            }
        }

        // for (String paragrafo : paragrafiList) { logger.info("Paragrafo: "+paragrafo); }

        logger.info("paragrafiList.size: " + paragrafiList.size());

        // se non ci sono paragrafi, allora divido in base al carattere "."
        // in genere non ci sono paragrafi quando il testo generato è corto.

        if (paragrafiList.size() <= 1) {
            rispostaSplitParagrafi = paragrafiList.get(0).split("\\.");
            paragrafiList.clear();
            for (String paragrafo : rispostaSplitParagrafi) {
                paragrafiList.add(paragrafo.trim() + ".");
            }
        }
        for (String paragrafo : paragrafiList) {
            logger.info("Paragrafo: " + paragrafo);
        }

        return paragrafiList;
    }
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