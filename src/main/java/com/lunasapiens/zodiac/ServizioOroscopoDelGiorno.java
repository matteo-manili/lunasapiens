package com.lunasapiens.zodiac;

import com.lunasapiens.GeneratorImage;
import com.lunasapiens.GeneratorVideo;
import com.lunasapiens.config.AppConfig;
import com.lunasapiens.Constants;
import com.lunasapiens.Utils;
import com.lunasapiens.config.PropertiesConfig;
import com.lunasapiens.dto.GiornoOraPosizioneDTO;
import com.lunasapiens.dto.OroscopoDelGiornoDescrizioneDTO;
import com.lunasapiens.entity.OroscopoGiornaliero;
import com.lunasapiens.repository.OroscopoGiornalieroRepository;
import com.lunasapiens.service.OroscopoGiornalieroService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

@Component
public class ServizioOroscopoDelGiorno {

    private static final Logger logger = LoggerFactory.getLogger(ServizioOroscopoDelGiorno.class);

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private PropertiesConfig propertiesConfig;

    @Autowired
    SegnoZodiacale segnoZodiacale;

    @Autowired
    private OroscopoGiornalieroService oroscopoGiornalieroService;

    @Autowired
    private OroscopoGiornalieroRepository oroscopoGiornalieroRepository;

    @Autowired
    private CacheManager cacheManager;


    // tokensRisposta signfiica i token da aggiungere oltre i token per la domanda
    private Double temperature = 0.3; private Integer tokensAggiuntiPerRisposta = 1000;
        private final Double finalcaratteriPerTokenStima = 10.0;



    public JSONObject jsonSchemaOrgTransitiDelGiorno(GiornoOraPosizioneDTO giornoOraPosizioneDTO) {
        // Aggiungi il creatore (esempio: l'organizzazione che ha creato i dati)
        JSONObject creator = new JSONObject();
        creator.put("@type", "Person");creator.put("name", "Matteo Manili");
        // Aggiunta del provider di dati
        JSONObject dataProvider = new JSONObject();
        dataProvider.put("@type", "Organization"); dataProvider.put("name", "LunaSapiens"); dataProvider.put("url", "https://www.lunasapiens.com");
        // Creazione dell'oggetto principale del dataset
        JSONObject dataset = new JSONObject();
        dataset.put("@context", "https://schema.org");
        dataset.put("@type", "Dataset");
        dataset.put("dataProvider", dataProvider);
        dataset.put("creator", creator);
        dataset.put("name", "Transiti Planetari del Giorno");
        dataset.put("description", "Transiti astrologici giornalieri per ogni pianeta, aggiornati quotidianamente.");
        dataset.put("license", "https://creativecommons.org/licenses/by/4.0/");
        dataset.put("datePublished", giornoOraPosizioneDTO.getJsonSchemaOrgGiornoInizio()); // Dovrebbe essere formato ISO 8601
        dataset.put("temporalCoverage", giornoOraPosizioneDTO.getJsonSchemaOrgGiornoFine()); // Dovrebbe essere formato ISO 8601

        // Lista dei transiti
        JSONArray transitiArray = new JSONArray();
        BuildInfoAstrologiaSwiss buildInfoAstroSwiss = new BuildInfoAstrologiaSwiss();
        // Aggiungi i pianeti con descrizione e retrogradazione
        for (Pianeti pianeta : buildInfoAstroSwiss.getPianetiTransiti(giornoOraPosizioneDTO, propertiesConfig.transitiSegniPianeti_OroscopoDelGiorno())) {
            if (pianeta.getNumeroPianeta() >= 0 && pianeta.getNumeroPianeta() <= 9) {
                JSONObject pianetaObject = new JSONObject();
                pianetaObject.put("name", pianeta.getNomePianeta());
                pianetaObject.put("sign", pianeta.getNomeSegnoZodiacale());
                pianetaObject.put("gradi", String.format("%.0f", pianeta.getGradi()));  // Arrotonda a due decimali
                pianetaObject.put("retrogrado", pianeta.isRetrogrado());  // Cambiato a booleano true/false
                pianetaObject.put("significato", pianeta.getSignificatoPianetaSegno());
                transitiArray.put(pianetaObject);
            }
        }
        // Aggiungi l'array di transiti al dataset
        dataset.put("variableMeasured", transitiArray);
        return dataset;
    }


    /**
     * questo va sulla pagina front-end
     */
    public OroscopoDelGiornoDescrizioneDTO descrizioneOroscopoDelGiorno(GiornoOraPosizioneDTO giornoOraPosizioneDTO) {
        String descrizioneOggi = "<p><b>Transiti:</b><br>";
        BuildInfoAstrologiaSwiss buildInfoAstroSwiss = new BuildInfoAstrologiaSwiss();
        ArrayList<Pianeti> pianetiTransiti = buildInfoAstroSwiss.getPianetiTransiti(giornoOraPosizioneDTO, propertiesConfig.transitiSegniPianeti_OroscopoDelGiorno());
        for (Pianeti var : pianetiTransiti) {
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
                descrizioneOggi += var.descrizione_Pianeta_Gradi_Retrogrado_SignificatoPianetaSegno_OROSCOPO() + "<br>";
            }
        }
        descrizioneOggi += "</p>";
        ArrayList<Aspetti> aspetti = CalcoloAspetti.aspettiListPinaneti(pianetiTransiti, propertiesConfig.aspettiSignificato());
        if(!aspetti.isEmpty()){
            descrizioneOggi += "<p><b>Aspetti:</b><br>";
            for(Aspetti var: aspetti) {
                descrizioneOggi += var.getNomePianeta_1()+ " e "+ var.getNomePianeta_2()+ " sono in "
                        +Constants.Aspetti.fromCode(var.getTipoAspetto()).getName()+"<br>";
            }
            descrizioneOggi += "</p>";
        }
        return new OroscopoDelGiornoDescrizioneDTO(descrizioneOggi, giornoOraPosizioneDTO);
    }




 /*
        // Lista degli aspetti
        JSONArray aspettiArray = new JSONArray();
        ArrayList<Aspetti> aspetti = CalcoloAspetti.aspettiListPinaneti(pianetiTransiti, propertiesConfig.aspettiPianeti());

        // Aggiungi gli aspetti
        for (Aspetti aspetto : aspetti) {
            JSONObject aspettoObject = new JSONObject();
            aspettoObject.put("pianeta1", aspetto.getNomePianeta_1());
            aspettoObject.put("pianeta2", aspetto.getNomePianeta_2());
            aspettoObject.put("aspetto", Constants.Aspetti.fromCode(aspetto.getTipoAspetto()).getName());
            aspettiArray.put(aspettoObject);
        }

        // Aggiungi l'array di aspetti al dataset (se non è vuoto)
        if (aspettiArray.length() > 0) {
            dataset.put("aspetti", aspettiArray);
        }
*/



    public StringBuilder oroscopoDelGiornoIA(Double temperature, int segno) {
        //########################################## INIZIO - INVIO LA DOMANDA ALLA IA #########################
        /*
        OpenAIGptTheokanning we = new OpenAIGptTheokanning();
        we.eseguiOpenAIGptTheokanning(appConfig.getParamOpenAi().getApiKeyOpenAI(), maxTokens, temperature, domandaBuilder.toString(),
                appConfig.getParamOpenAi().getModelGpt4() );

        we.eseguiOpenAIGptTheokanning(appConfig.getParamOpenAi().getApiKeyOpenAI(), maxTokens, temperature, domandaBuilder.toString(),
                appConfig.getParamOpenAi().getModelGpt3_5());
         */

        StringBuilder domanda = domanda_prompt(segno);
        //logger.info("DOMANDA: " + domanda);

        //OpenAIGptAzure openAIGptAzure = new OpenAIGptAzure();
        //return openAIGptAzure.eseguiOpenAIGptAzure_Instruct(appConfig.getParamOpenAi().getApiKeyOpenAI(), maxTokens, temperature, domanda.toString(),
        //      appConfig.getParamOpenAi().getModelGpt3_5TurboInstruct() );

        // return openAIGptTheokanning.eseguiOpenAIGptTheokanning(appConfig.getParamOpenAi().getApiKeyOpenAI(), maxTokens, temperature, domanda.toString(),
        //                appConfig.getParamOpenAi().getModelGpt4() );

        OpenAIGptTheokanning openAIGptTheokanning = new OpenAIGptTheokanning();
        return openAIGptTheokanning.eseguiOpenAIGptTheokanning(appConfig.getParamOpenAi().getApiKeyOpenAI(), temperature, tokensAggiuntiPerRisposta, finalcaratteriPerTokenStima,
                appConfig.getParamOpenAi().getModelGpt4_Mini(), domanda.toString() );
    }


    public StringBuilder domanda_prompt(int numeroSegno) {
        Properties aspettiSignProperties = propertiesConfig.aspettiSignificato();
        Properties pianetaRetrogradoProperties = propertiesConfig.pianetaRetrogrado();
        Properties pianetiOroscopoSignificatoProperties = propertiesConfig.pianetiOroscopoSignificato();

        GiornoOraPosizioneDTO giornoOraPosizioneDTO = Utils.GiornoOraPosizione_OggiRomaOre12();
        BuildInfoAstrologiaSwiss buildInfoAstroSwiss = new BuildInfoAstrologiaSwiss();
        ArrayList<Pianeti> pianeta = buildInfoAstroSwiss.getPianetiTransiti(giornoOraPosizioneDTO, propertiesConfig.transitiSegniPianeti_OroscopoDelGiorno());

        logger.info( "---------- Inizio!!! segno "+numeroSegno +" ----------" );

        StringBuilder domandaBuilder = new StringBuilder();

        String inizioDomanda = "Tu sei un astrologo che genera l'oroscopo del giono in base ai dati forniti, senza inventare e aggiungere nulla. \n" +
                "Scrivi l'oroscopo del giorno per il segno del " +Constants.SegniZodiacali.fromNumero(numeroSegno).getNome()+".\n" +
                "L'oroscopo deve argomentare gli eventi di oggi declinandoli agli aspetti. \n" +
                "Se è possibile oroganizza l'orosocpo dividendo il testo con questi argomenti: " +
                "Amore e Relazioni, " +
                "Famiglia e Amicizie, " +
                "Salute e Benessere, " +
                "Carriera e Lavoro, " +
                "Finanze e Denaro." + "\n\n";
        domandaBuilder.append(inizioDomanda);

        domandaBuilder.append("- Descrizione segno zodiacale:\n" );
        SegnoZodiacale segnoZod = segnoZodiacale.getSegnoZodiacale( numeroSegno );
        domandaBuilder.append( segnoZod.getDescrizioneMin() + "\n \n");
        //String segnoDescrizione100Caratteri = segnoZod.getDescrizione().substring(0, Math.min(segnoZod.getDescrizione().length(), 300));
        //domandaBuilder.append( segnoDescrizione100Caratteri + "\n\n");


        int[] pianetiSignori = segnoZod.getPianetiSignoreDelSegno();
        ArrayList<Aspetti> aspettiTuttiList = CalcoloAspetti.aspettiListPinaneti(pianeta, aspettiSignProperties);
        List<Integer> aspettiPresentiNelSegno = new ArrayList<>();
        boolean presentePianetaRetrogrado = false; boolean presentiAspetti = false; int contaEventi = 1;

        // TreeSet: mantiene gli elementi unici e ordinati in ordine crescente
        TreeSet<Integer> pianetiCoinvoltiSet = new TreeSet<>();

        domandaBuilder.append("- Eventi di oggi:\n" );
        for (int pianetaSig : pianetiSignori) {
            ArrayList<Aspetti> aspettiDelSegnoList = getAspettiPianetaList(aspettiTuttiList, pianetaSig);
            if(aspettiDelSegnoList.isEmpty()) {
                Pianeti pianetaSenzaAspetti = getPianetaPosizTransitoSegno(pianeta, pianetaSig);
                domandaBuilder.append("Evento numero "+contaEventi+":\n"); contaEventi++;
                domandaBuilder.append(pianetaSenzaAspetti.getNomePianeta() + ": " + pianetaSenzaAspetti.getSignificatoPianetaSegno()+"\n");
                pianetiCoinvoltiSet.add(pianetaSenzaAspetti.getNumeroPianeta());
                if(pianetaSenzaAspetti.isRetrogrado()){
                    domandaBuilder.append( "Tipo di Aspetto: "+pianetaRetrogradoString(pianetaSenzaAspetti.getNomePianeta())+"\n");
                }
                domandaBuilder.append("\n");
                presentePianetaRetrogrado = presentePianetaRetrogrado || pianetaSenzaAspetti.isRetrogrado() ? true : false;
            }else{
                presentiAspetti = true;
                for(Aspetti aspettodelSegno: aspettiDelSegnoList) {
                    aspettiPresentiNelSegno.add(aspettodelSegno.getTipoAspetto());
                    Pianeti pianetaTransito_1 = getPianetaPosizTransitoSegno(pianeta, aspettodelSegno.getNumeroPianeta_1());
                    Pianeti pianetaTransito_2 = getPianetaPosizTransitoSegno(pianeta, aspettodelSegno.getNumeroPianeta_2());
                    domandaBuilder.append("Evento numero "+contaEventi+":\n"); contaEventi++;
                    pianetiCoinvoltiSet.add(pianetaTransito_1.getNumeroPianeta());
                    pianetiCoinvoltiSet.add(pianetaTransito_2.getNumeroPianeta());
                    if( pianetaTransito_1.getNumeroPianeta() == pianetaSig  ){
                        domandaBuilder.append(pianetaTransito_1.getNomePianeta() + ": " + pianetaTransito_1.getSignificatoPianetaSegno()+"\n");
                        domandaBuilder.append(pianetaTransito_2.getNomePianeta() + ": " + pianetaTransito_2.getSignificatoPianetaSegno()+"\n");
                        presentePianetaRetrogrado = presentePianetaRetrogrado || pianetaTransito_1.isRetrogrado() || pianetaTransito_2.isRetrogrado() ? true : false;

                    }else{
                        domandaBuilder.append(pianetaTransito_2.getNomePianeta() + ": " + pianetaTransito_2.getSignificatoPianetaSegno()+"\n");
                        domandaBuilder.append(pianetaTransito_1.getNomePianeta() + ": " + pianetaTransito_1.getSignificatoPianetaSegno()+"\n");
                        presentePianetaRetrogrado = presentePianetaRetrogrado || pianetaTransito_2.isRetrogrado() || pianetaTransito_1.isRetrogrado() ? true : false;
                    }
                    domandaBuilder.append("Tipo di Aspetto: "+aspettodelSegno.getTitoloAspetto() +".\n");
                    if(pianetaTransito_1.isRetrogrado()){
                        domandaBuilder.append( pianetaRetrogradoString(pianetaTransito_1.getNomePianeta())+"\n");
                    }
                    if(pianetaTransito_2.isRetrogrado()){
                        domandaBuilder.append( "Tipo di Aspetto: "+pianetaRetrogradoString(pianetaTransito_2.getNomePianeta())+"\n");
                    }
                }
            }
        }

        domandaBuilder.append("\n");
        domandaBuilder.append("- Significato dei Pianeti:\n");
        for (Integer numeroPianetq : pianetiCoinvoltiSet) {
            domandaBuilder.append(pianetiOroscopoSignificatoProperties.getProperty( String.valueOf(numeroPianetq)+"_min")+"\n" );
        }

        if(presentiAspetti){
            domandaBuilder.append("\n");
            domandaBuilder.append("- Significato degli Aspetti:\n");
            for (Constants.Aspetti aspetti : Constants.Aspetti.values()) {
                if(aspettiPresentiNelSegno.contains(aspetti.getCode())) {
                    domandaBuilder.append(aspetti.getName()+": "+aspettiSignProperties.getProperty( String.valueOf(aspetti.getCode())+"_min")+"\n" );
                }
            }
        }
        // Nella IA lo tratto come se fosse un aspetto.
        if(presentePianetaRetrogrado){
            domandaBuilder.append(pianetaRetrogradoProperties.getProperty( String.valueOf(0) ));
        }
        logger.info( domandaBuilder.toString() );
        logger.info( "---------- fine segno "+numeroSegno +" ----------" );
        return domandaBuilder;
    }

    private String pianetaRetrogradoString(String nomePianeta){
        return nomePianeta +" è Pianeta Retrogrado.";
    }

    private Pianeti getPianetaPosizTransitoSegno(ArrayList<Pianeti> pianetaTuttiList, int pianeta) {
        Pianeti pianetaPosizTransito = new Pianeti();
        for(Pianeti var : pianetaTuttiList) {
            if(var.getNumeroPianeta() == pianeta){
                pianetaPosizTransito = var;
            }
        }
        return pianetaPosizTransito;
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



    public void creaOroscopoGiornaliero() {
        String pathOroscopoGiornalieroImmagini = Constants.PATH_STATIC + GeneratorImage.folderOroscopoGiornalieroImmagine;
        GiornoOraPosizioneDTO giornoOraPosizioneDTO = Utils.GiornoOraPosizione_OggiRomaOre12();

        Cache cache = cacheManager.getCache(Constants.VIDEO_CACHE);
        cache.invalidate();

        logger.info("elimino cartelle e file dal classpath...");
        // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ ELIMINO LE CARTELLE E FILE @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
        Utils.eliminaCartelleEFile(pathOroscopoGiornalieroImmagini);

        for (int numeroSegno = 0; numeroSegno <= 11; numeroSegno++) {
            OroscopoGiornaliero oroscopoGiornaliero = oroscopoGiornalieroService.findByNumSegnoAndDataOroscopo(numeroSegno, Utils.convertiGiornoOraPosizioneDTOInDate(giornoOraPosizioneDTO));

            if (oroscopoGiornaliero == null || oroscopoGiornaliero.getVideo() == null || oroscopoGiornaliero.getNomeFileVideo() == null
                    || oroscopoGiornaliero.getTestoOroscopo() == null) {

                if(oroscopoGiornaliero == null) {
                    oroscopoGiornaliero = new OroscopoGiornaliero();
                    oroscopoGiornaliero.setNumSegno(numeroSegno);
                    oroscopoGiornaliero.setDataOroscopo( Utils.convertiGiornoOraPosizioneDTOInDate(giornoOraPosizioneDTO) );
                }
                try {
                    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ CREAZIONE CONTENUTO IA @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                    StringBuilder sBTestoOroscopoIA = null;
                    if(oroscopoGiornaliero.getTestoOroscopo() == null || oroscopoGiornaliero.getTestoOroscopo().isEmpty()) {
                        sBTestoOroscopoIA = oroscopoDelGiornoIA(temperature, numeroSegno);
                        if(sBTestoOroscopoIA == null || sBTestoOroscopoIA.length() == 0) {
                            logger.error("sBTestoOroscopo null: Risposta nulla dalla IA. Salto iterazione del ciclo della creazione del video");
                            // l'istruzione continue viene eseguita, facendo saltare l'iterazione corrente e passando direttamente alla successiva.
                            continue;
                        }
                    } else {
                        sBTestoOroscopoIA = new StringBuilder( oroscopoGiornaliero.getTestoOroscopo() );
                    }

                    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ LAVORAZIONE TESTO @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                    ArrayList<String> paragrafiTestoOroscopoIA = ServizioOroscopoDelGiorno.dividiParagrafiStringBuilderIA( sBTestoOroscopoIA );


                    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ CREAZIONE IMMAGINE @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                    String fontName = "Comic Sans MS"; // Arial
                    int fontSize = 25; Color textColor = Color.BLUE;

                    // Formattatore per la data
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    String dataOroscopoString = formatter.format( Utils.convertiGiornoOraPosizioneDTOInDate(giornoOraPosizioneDTO) );

                    String imagePath = pathOroscopoGiornalieroImmagini + dataOroscopoString + "/" + numeroSegno + "/";
                    GeneratorImage igenerat = new GeneratorImage();
                    // Itera su ogni pezzo della stringa
                    for (int i = 0; i < paragrafiTestoOroscopoIA.size(); i++) {
                        String fileName = i + ".png";
                        String imagePathFileName = imagePath + fileName;
                        igenerat.generateImage(paragrafiTestoOroscopoIA.get(i), fontName, fontSize, textColor, imagePathFileName);
                    }

                    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ CREAZIONE VIDEO @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                    String nomeFileVideo = dataOroscopoString + "_" + numeroSegno;
                    byte[] videoBytes = GeneratorVideo.createVideoFromImages(imagePath, nomeFileVideo );

                    try{
                        oroscopoGiornaliero = oroscopoGiornalieroService.salvaOroscoopoGiornaliero(numeroSegno, sBTestoOroscopoIA, giornoOraPosizioneDTO,
                                videoBytes, nomeFileVideo + GeneratorVideo.formatoVideo());

                    } catch (DataIntegrityViolationException e) {
                        oroscopoGiornaliero = oroscopoGiornalieroService.findByNumSegnoAndDataOroscopo(numeroSegno, Utils.convertiGiornoOraPosizioneDTOInDate(giornoOraPosizioneDTO));
                        oroscopoGiornaliero.setNumSegno(numeroSegno);
                        oroscopoGiornaliero.setTestoOroscopo(sBTestoOroscopoIA.toString());
                        oroscopoGiornaliero.setDataOroscopo( Utils.convertiGiornoOraPosizioneDTOInDate(giornoOraPosizioneDTO) );
                        oroscopoGiornaliero.setVideo(videoBytes);
                        oroscopoGiornaliero.setNomeFileVideo(nomeFileVideo + GeneratorVideo.formatoVideo());
                        oroscopoGiornalieroRepository.save(oroscopoGiornaliero);
                    }
                } catch (Exception e) {
                    logger.info("Error in creaOroscopoGiornaliero(): " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        logger.info("faccio pausa Thread.sleep......");
        try{
            // Faccio una pausa per smalite le operazioni fatte
            Thread.sleep(10000); // 10000 = 10 secondi
        }catch(InterruptedException e){
            e.printStackTrace();
        }

        logger.info("elimino cartelle e file dal classpath...");
        // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ ELIMINO LE CARTELLE E FILE @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
        Utils.eliminaCartelleEFile(pathOroscopoGiornalieroImmagini);

        logger.info("metto i video in cache...");
        // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ SALVA VIDEO SU NELLA CACHE @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
        java.util.List<OroscopoGiornaliero> listOroscopoGiorn = oroscopoGiornalieroService.findAllByDataOroscopo(Utils.OggiRomaOre12());
        for (OroscopoGiornaliero oroscopoGiorno : listOroscopoGiorn) {
            if(oroscopoGiorno.getVideo() != null){
                cache.put(oroscopoGiorno.getNomeFileVideo(), Utils.VideoResponseEntityByteArrayResource(oroscopoGiorno.getVideo()));
            }
        }
        logger.info("Fine Task creaOroscopoGiornaliero.");
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