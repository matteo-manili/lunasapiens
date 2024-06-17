package com.lunasapiens.zodiac;

import com.lunasapiens.AppConfig;
import com.lunasapiens.Constants;
import com.lunasapiens.Util;
import com.lunasapiens.dto.GiornoOraPosizioneDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@Component
public class ServiziAstrologici {

    private static final Logger logger = LoggerFactory.getLogger(ServiziAstrologici.class);

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private BuildInfoAstrologiaSwiss buildInfoAstroSwiss;

    private Double temperature = 0.3; private Integer maxTokens = 2000;



    public StringBuilder oroscopoDelGiornoIA(int segno, GiornoOraPosizioneDTO giornoOraPosizioneDTO) {
        return oroscopoDelGiorno(temperature, maxTokens, segno, giornoOraPosizioneDTO);
    }


    public void test_Oroscopo_Segni_Transiti_Aspetti() {
        for(int numeroSegno = 4; numeroSegno <= 9; numeroSegno++) {
            test_Oroscopo_Segni_Transiti_Aspetti(numeroSegno);
        }
    }


    public StringBuilder test_Oroscopo_Segni_Transiti_Aspetti(int numeroSegno) {
        Properties segniZodDecrizProperties = appConfig.segniZodiacali();
        Properties aspettiPianetiProperties = appConfig.AspettiPianeti();
        Properties pianetaRetrogradoProperties = appConfig.pianetaRetrogrado();

        GiornoOraPosizioneDTO giornoOraPosizioneDTO = Util.GiornoOraPosizione_OggiRomaOre12();
        ArrayList<PianetaPosizTransito> pianetaPosizTransito = buildInfoAstroSwiss.getPianetiTransiti(giornoOraPosizioneDTO);


        System.out.println( "---------- Inizio!!! segno "+numeroSegno +" ----------" );

        SegnoZodiacale segnoZodiacale = new SegnoZodiacale();
        StringBuilder domandaBuilder = new StringBuilder();

        domandaBuilder.append("Tu sei un astrologo che risponde in base ai dati forniti, senza inventare e aggiungere nulla.\n" +
                "Scrivi l'oroscopo del giorno per l'utente del segno del " +Constants.SegniZodiacali.fromNumero(numeroSegno).getNome()+". L'oroscopo deve argomentare gli eventi di oggi declinandoli agli aspetti.\n\n" );


        domandaBuilder.append("- Descrizione segno zodiacale:\n" );
        SegnoZodiacale segnoZod = segnoZodiacale.getSegnoZodiacale( numeroSegno, segniZodDecrizProperties );
        domandaBuilder.append( segnoZod.getDescrizioneMin() + "\n \n");
        //String segnoDescrizione100Caratteri = segnoZod.getDescrizione().substring(0, Math.min(segnoZod.getDescrizione().length(), 300));
        //domandaBuilder.append( segnoDescrizione100Caratteri + "\n\n");



        int[] pianetiSignori = segnoZod.getPianetiSignoreDelSegno();
        ArrayList<Aspetti> aspettiTuttiList = CalcoloAspetti.verificaAspetti(pianetaPosizTransito, aspettiPianetiProperties);
        List<Integer> aspettiPresentiNelSegno = new ArrayList<>();
        boolean presentePianetaRetrogrado = false;
        boolean presentiAspetti = false;
        int contaEventi = 1;
        domandaBuilder.append("- Eventi di oggi:\n" );
        for (int pianetaSig : pianetiSignori) {
            ArrayList<Aspetti> aspettiDelSegnoList = getAspettiPianetaList(aspettiTuttiList, pianetaSig);

            if(aspettiDelSegnoList.isEmpty()) {
                PianetaPosizTransito pianetaSenzaAspetti = getPianetaPosizTransitoSegno(pianetaPosizTransito, pianetaSig);
                domandaBuilder.append("Evento numero "+contaEventi+":\n"); contaEventi++;
                domandaBuilder.append(pianetaSenzaAspetti.getNomePianeta() + pianetaRetrogradoString(pianetaSenzaAspetti.isRetrogrado()) +
                        pianetaSenzaAspetti.getSignificatoPianetaSegno()+"\n\n");
                presentePianetaRetrogrado = presentePianetaRetrogrado || pianetaSenzaAspetti.isRetrogrado() ? true : false;

            }else{
                presentiAspetti = true;
                for(Aspetti aspettodelSegno: aspettiDelSegnoList) {
                    aspettiPresentiNelSegno.add(aspettodelSegno.getTipoAspetto());
                    PianetaPosizTransito pianetaTransito_1 = getPianetaPosizTransitoSegno(pianetaPosizTransito, aspettodelSegno.getNumeroPianeta_1());
                    PianetaPosizTransito pianetaTransito_2 = getPianetaPosizTransitoSegno(pianetaPosizTransito, aspettodelSegno.getNumeroPianeta_2());
                    domandaBuilder.append("Evento numero "+contaEventi+":\n"); contaEventi++;
                    if( pianetaTransito_1.getNumeroPianeta() == pianetaSig  ){
                        domandaBuilder.append(pianetaTransito_1.getNomePianeta() + pianetaRetrogradoString(pianetaTransito_1.isRetrogrado()) +
                                pianetaTransito_1.getSignificatoPianetaSegno()+"\n");
                        domandaBuilder.append(pianetaTransito_2.getNomePianeta() + pianetaRetrogradoString(pianetaTransito_2.isRetrogrado()) +
                                pianetaTransito_2.getSignificatoPianetaSegno()+"\n");
                        presentePianetaRetrogrado = presentePianetaRetrogrado || pianetaTransito_1.isRetrogrado() || pianetaTransito_2.isRetrogrado() ? true : false;

                    }else{
                        domandaBuilder.append(pianetaTransito_2.getNomePianeta() + pianetaRetrogradoString(pianetaTransito_2.isRetrogrado()) +
                                pianetaTransito_2.getSignificatoPianetaSegno()+"\n");
                        domandaBuilder.append(pianetaTransito_1.getNomePianeta() + pianetaRetrogradoString(pianetaTransito_1.isRetrogrado()) +
                                pianetaTransito_1.getSignificatoPianetaSegno()+"\n");
                        presentePianetaRetrogrado = presentePianetaRetrogrado || pianetaTransito_2.isRetrogrado() || pianetaTransito_1.isRetrogrado() ? true : false;
                    }
                    domandaBuilder.append("Tipo di Aspetto: "+aspettodelSegno.getTitoloAspetto() +"\n\n");
                }
            }
        }


        if(presentiAspetti){
            domandaBuilder.append("\n");
            domandaBuilder.append("- Significato degli Aspetti:\n");
            for (Constants.Aspetti aspetti : Constants.Aspetti.values()) {
                if(aspettiPresentiNelSegno.contains(aspetti.getCode())) {
                    domandaBuilder.append(aspetti.getName()+": "+aspettiPianetiProperties.getProperty( String.valueOf(aspetti.getCode()))+"\n\n" );
                }
            }
        }


        if(presentePianetaRetrogrado){
            domandaBuilder.append("\n");
            domandaBuilder.append("- Significato pianeta Retrogrado:\n");
            domandaBuilder.append(pianetaRetrogradoProperties.getProperty( "0" ));
        }


        System.out.println( domandaBuilder.toString() );
        System.out.println( "---------- fine segno "+numeroSegno +" ----------" );

        return domandaBuilder;
    }

    private String pianetaRetrogradoString(boolean pianetaRetrogrado){
        return pianetaRetrogrado ? " ("+Constants.PIANETA_RETROGRADO+"): " : ": ";
    }

    private ArrayList<PianetaPosizTransito> getPianetiPosizTransitoSegnoList(ArrayList<PianetaPosizTransito> pianetaPosizTransito, int[] pianetiSignor) {
        ArrayList<PianetaPosizTransito> pianetaPosizTransitoList = new ArrayList<>();;
        for(PianetaPosizTransito var : pianetaPosizTransito) {
            for (int pianetaSig : pianetiSignor) {
                if(var.getNumeroPianeta() == pianetaSig){
                    pianetaPosizTransitoList.add(var);
                }
            }
        }
        return pianetaPosizTransitoList;
    }

    private PianetaPosizTransito getPianetaPosizTransitoSegno(ArrayList<PianetaPosizTransito> pianetaPosizTransitoTuttiList, int pianeta) {
        PianetaPosizTransito pianetaPosizTransito = new PianetaPosizTransito();
        for(PianetaPosizTransito var : pianetaPosizTransitoTuttiList) {
            if(var.getNumeroPianeta() == pianeta){
                pianetaPosizTransito = var;
            }
        }
        return pianetaPosizTransito;
    }

    private ArrayList<Aspetti> getAspettiPianetiSignoriList(ArrayList<Aspetti> aspetti, int[] pianetiSignor) {
        ArrayList<Aspetti> aspettiSegnoList = new ArrayList<>();;
        if(!aspetti.isEmpty()){
            for(Aspetti var: aspetti) {
                int numeroPianeta1 = var.getNumeroPianeta_1(); int numeroPianeta2 = var.getNumeroPianeta_2();
                boolean pianeta1Presente = Arrays.stream(pianetiSignor).anyMatch(p -> p == var.getNumeroPianeta_1());
                boolean pianeta2Presente = Arrays.stream(pianetiSignor).anyMatch(p -> p == var.getNumeroPianeta_2());
                if (pianeta1Presente || pianeta2Presente) {
                    aspettiSegnoList.add(var);
                }
            }
        }
        return aspettiSegnoList;
    }

    private ArrayList<Aspetti> getAspettiPianetaList(ArrayList<Aspetti> aspetti, int pianeta) {
        ArrayList<Aspetti> aspettiSegnoList = new ArrayList<>();;
        if (!aspetti.isEmpty()) {
            for (Aspetti var : aspetti) {
                int numeroPianeta1 = var.getNumeroPianeta_1(); int numeroPianeta2 = var.getNumeroPianeta_2();
                // Controlla se uno dei due pianeti è uguale al pianeta passato
                if (numeroPianeta1 == pianeta || numeroPianeta2 == pianeta) {
                    aspettiSegnoList.add(var);
                }
            }
        }
        return aspettiSegnoList;
    }





    public String oroscopoDelGiornoDescrizioneOggi(GiornoOraPosizioneDTO giornoOraPosizioneDTO) {
        String descrizioneOggi = "Oggi è: " + giornoOraPosizioneDTO.getGiorno() + "/" + giornoOraPosizioneDTO.getMese() + "/" + giornoOraPosizioneDTO.getAnno()
                + " ore " + String.format("%02d", giornoOraPosizioneDTO.getOra()) + ":" + String.format("%02d", giornoOraPosizioneDTO.getMinuti()) + "\n" +
                "Transiti: ";
        ArrayList<PianetaPosizTransito> pianetiTransiti = buildInfoAstroSwiss.getPianetiTransiti(giornoOraPosizioneDTO);
        for (PianetaPosizTransito var : pianetiTransiti) {
            if (var.getNumeroPianeta() == Constants.Pianeti.fromNumero(0).getNumero() ||
                    var.getNumeroPianeta() == Constants.Pianeti.fromNumero(1).getNumero() ||
                    var.getNumeroPianeta() == Constants.Pianeti.fromNumero(2).getNumero() ||
                    var.getNumeroPianeta() == Constants.Pianeti.fromNumero(3).getNumero() ||
                    var.getNumeroPianeta() == Constants.Pianeti.fromNumero(4).getNumero() ||
                    var.getNumeroPianeta() == Constants.Pianeti.fromNumero(5).getNumero() ||
                    var.getNumeroPianeta() == Constants.Pianeti.fromNumero(6).getNumero() ||
                    var.getNumeroPianeta() == Constants.Pianeti.fromNumero(7).getNumero() ||
                    var.getNumeroPianeta() == Constants.Pianeti.fromNumero(8).getNumero() ||
                    var.getNumeroPianeta() == Constants.Pianeti.fromNumero(9).getNumero() ) {
                descrizioneOggi += var.descrizione_Pianeta_Gradi_Retrogrado_SignificatoPianetaSegno() + "\n";
            }
        }

        ArrayList<Aspetti> aspetti = CalcoloAspetti.verificaAspetti(pianetiTransiti, appConfig.AspettiPianeti());
        if(!aspetti.isEmpty()){
            descrizioneOggi += "\n" + "Aspetti: ";
            for(Aspetti var: aspetti) {
                descrizioneOggi += var.getNomePianeta_1() + " e "+ var.getNomePianeta_2() + " sono in "+ Constants.Aspetti.fromCode(var.getTipoAspetto()).getName()+"\n";
            }
        }

        // TODO le case placide non le uso più per l'oroscopo giornaliero

        return descrizioneOggi;
    }


    public StringBuilder oroscopoDelGiorno(Double temperature, Integer maxTokens, int segno, GiornoOraPosizioneDTO giornoOraPosizioneDTOaa) {

        System.out.println("############################ TEXT IA ###################################");



        // TODO le case placide non le uso più per l'oroscopo giornaliero
        /*
        domandaBuilder.append("\n").append("Case Placide: ");
        for (CasePlacide var : buildInfoAstroSwiss.getCasePlacide(giornoOraPosizioneDTO)) {
            domandaBuilder.append(var.descrizioneCasaGradi());
            System.out.println( var.toString() );
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

        StringBuilder domanda = test_Oroscopo_Segni_Transiti_Aspetti(segno);
        //logger.info("DOMANDA: " + domanda);

        OpenAIGptAzure openAIGptAzure = new OpenAIGptAzure();
        return openAIGptAzure.eseguiOpenAIGptAzure_Instruct(appConfig.getParamOpenAi().getApiKeyOpenAI(), maxTokens, temperature, domanda.toString(),
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