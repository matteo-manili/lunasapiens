package com.lunasapiens.zodiac;

import com.lunasapiens.AppConfig;
import com.lunasapiens.Constants;
import com.lunasapiens.dto.GiornoOraPosizioneDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;

@Component
public class ServiziAstrologici {

    private static final Logger logger = LoggerFactory.getLogger(ServiziAstrologici.class);

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private BuildInfoAstrologiaSwiss buildInfoAstroSwiss;


    private Double temperature = 1.0; private Integer maxTokens = 800;


    public StringBuilder oroscopoDelGiornoIA(String segno, GiornoOraPosizioneDTO giornoOraPosizioneDTO) {
        return oroscopoDelGiorno(temperature, maxTokens, segno, giornoOraPosizioneDTO);
    }


    public String oroscopoDelGiornoDescrizioneOggi(GiornoOraPosizioneDTO giornoOraPosizioneDTO) {
        String descrizioneOggi = "Oggi è: " + giornoOraPosizioneDTO.getGiorno() + "/" + giornoOraPosizioneDTO.getMese() + "/" + giornoOraPosizioneDTO.getAnno()
                + " ore " + String.format("%02d", giornoOraPosizioneDTO.getOra()) + ":" + String.format("%02d", giornoOraPosizioneDTO.getMinuti()) + "\n" +
                "Transiti: ";
        ArrayList<PianetaPosizione> pianetiTransiti = buildInfoAstroSwiss.getPianetiTransiti(giornoOraPosizioneDTO);

        for (PianetaPosizione var : pianetiTransiti) {
            if (var.getNomePianeta().equals(Constants.Pianeti[0]) ||
                    var.getNomePianeta().equals(Constants.Pianeti[1]) ||
                    var.getNomePianeta().equals(Constants.Pianeti[2]) ||
                    var.getNomePianeta().equals(Constants.Pianeti[3]) ||
                    var.getNomePianeta().equals(Constants.Pianeti[4]) ||
                    var.getNomePianeta().equals(Constants.Pianeti[5]) ||
                    var.getNomePianeta().equals(Constants.Pianeti[6]) ||
                    var.getNomePianeta().equals(Constants.Pianeti[7]) ||
                    var.getNomePianeta().equals(Constants.Pianeti[8]) ||
                    var.getNomePianeta().equals(Constants.Pianeti[9]) ) {
                descrizioneOggi += var.descrizione_Pianeta_Gradi_Retrogrado_SignificatoPianetaSegnoRiguardaSolo() + "\n" ;
            }
        }

        PianetiAspetti pianetiAspetti = new PianetiAspetti();
        ArrayList<String> aspetti = pianetiAspetti.verificaAspetti(pianetiTransiti);
        if(aspetti.size() > 0) {
            descrizioneOggi += "\n" + "Aspetti: ";
            for(String var : aspetti){
                descrizioneOggi += var + ". ";
            }
        }

        // TODO le case placide non le uso più per l'oroscopo giornaliero
        //descrizioneOggi += "\n" + "Case Placide di oggi: ";
        //for (CasePlacide var : buildInfoAstroSwiss.getCasePlacide(giornoOraPosizioneDTO)) {
        //    descrizioneOggi += var.descrizioneCasa();
        //}

        return descrizioneOggi;
    }


    public StringBuilder oroscopoDelGiorno(Double temperature, Integer maxTokens, String segno, GiornoOraPosizioneDTO giornoOraPosizioneDTO) {

        System.out.println("############################ TEXT IA ###################################");

        StringBuilder domandaBuilder = new StringBuilder();
        domandaBuilder.append("Crea l'oroscopo del giorno basandoti sui seguenti dati per il segno del ").append(segno).append(". ").append("\n")
                .append("Oggi è: ").append(giornoOraPosizioneDTO.getGiorno()).append("/").append(giornoOraPosizioneDTO.getMese()).append("/")
                .append(giornoOraPosizioneDTO.getAnno()).append(" ore ").append(giornoOraPosizioneDTO.getOra()).append(":")
                .append(giornoOraPosizioneDTO.getMinuti()).append("\n").append("Transiti: ");

        ArrayList<PianetaPosizione> pianetiTransiti = buildInfoAstroSwiss.getPianetiTransiti(giornoOraPosizioneDTO);
        for (PianetaPosizione var : pianetiTransiti) {
            if (Arrays.asList(Constants.Pianeti).contains(var.getNomePianeta())) {
                domandaBuilder.append(var.descrizione_Pianeta_Gradi_Retrogrado_SignificatoPianetaSegnoRiguardaSolo());
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

        // TODO le case placide non le uso più per l'oroscopo giornaliero
        //domandaBuilder.append("\n").append("Case Placide: ");
        //for (CasePlacide var : buildInfoAstroSwiss.getCasePlacide(giornoOraPosizioneDTO)) {
        //    domandaBuilder.append(var.descrizioneCasaGradi());
            //System.out.println( var.toString() );
        //}


        logger.info("DOMANDA: " + domandaBuilder.toString());


        /*
        BuildInfoAstrologiaAstroLib buildInfoAstroAstroLib = new BuildInfoAstrologiaAstroLib(giornoOraPosizioneDTO);
        buildInfoAstroAstroLib.getCasePlacide();
        for (CasePlacide var : buildInfoAstroAstroLib.getCasePlacide()) {
            System.out.println("AstroLib: "+ var.toString() );
        }
         */


        //########################################## INIZIO - INVIO LA DOMANDA ALLA IA #########################

        /*
        OpenAIGptTheokanning we = new OpenAIGptTheokanning();
        we.eseguiOpenAIGptTheokanning(appConfig.getParamOpenAi().getApiKeyOpenAI(), maxTokens, temperature, domandaBuilder.toString(),
                appConfig.getParamOpenAi().getModelGpt4() );

        we.eseguiOpenAIGptTheokanning(appConfig.getParamOpenAi().getApiKeyOpenAI(), maxTokens, temperature, domandaBuilder.toString(),
                appConfig.getParamOpenAi().getModelGpt3_5());

         */

        OpenAIGptAzure openAIGptAzure = new OpenAIGptAzure();
        return openAIGptAzure.eseguiOpenAIGptAzure_Instruct(appConfig.getParamOpenAi().getApiKeyOpenAI(), maxTokens, temperature, domandaBuilder.toString(),
                appConfig.getParamOpenAi().getModelGpt3_5TurboInstruct() );


        //########################################## FINE #########################

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