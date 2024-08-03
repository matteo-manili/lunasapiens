package com.lunasapiens.zodiac;

import com.lunasapiens.config.AppConfig;
import com.lunasapiens.Constants;
import com.lunasapiens.dto.CoordinateDTO;
import com.lunasapiens.dto.GiornoOraPosizioneDTO;
import com.theokanning.openai.completion.chat.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
public class ServizioTemaNatale {

    private static final Logger logger = LoggerFactory.getLogger(ServizioTemaNatale.class);

    @Autowired
    private AppConfig appConfig;

    @Autowired
    SegnoZodiacale segnoZodiacale;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CacheManager cacheManager;


    // tokensRisposta signfiica i token da aggiungere oltre i token per la domanda
    private Double temperature = 0.3; private Integer tokensRisposta = 500;



    public StringBuilder chatBotTemaNatale( List<ChatMessage> chatMessageList ) {
        OpenAIGptTheokanning openAIGptTheokanning = new OpenAIGptTheokanning();
        return openAIGptTheokanning.eseguiOpenAIGptTheokanning(appConfig.getParamOpenAi().getApiKeyOpenAI(), temperature, tokensRisposta,
                appConfig.getParamOpenAi().getModelGpt4_Mini(), chatMessageList );
    }



    public String temaNataleDescrizione_AstrologiaAstroSeek(GiornoOraPosizioneDTO giornoOraPosizioneDTO, CoordinateDTO coordinateDTO) {
        BuildInfoAstrologiaAstroSeek buildInfoAstrologiaAstroSeek = new BuildInfoAstrologiaAstroSeek();
        BuildInfoAstrologiaAstroSeek result = buildInfoAstrologiaAstroSeek.catturaTemaNataleAstroSeek(restTemplate, cacheManager.getCache(Constants.URLS_ASTRO_SEEK_CACHE), giornoOraPosizioneDTO, coordinateDTO, appConfig.transitiPianetiSegni_TemaNatale() );

        return temaNataleDescrizione(result.getPianetaPosizTransitoArrayList(), result.getCasePlacidesArrayList());
    }


    public String temaNataleDescrizione_AstrologiaSwiss(GiornoOraPosizioneDTO giornoOraPosizioneDTO) {
        BuildInfoAstrologiaSwiss buildInfoAstroSwiss = new BuildInfoAstrologiaSwiss();
        ArrayList<PianetaPosizTransito> pianetiTransiti = buildInfoAstroSwiss.getPianetiTransiti(giornoOraPosizioneDTO, appConfig.transitiPianetiSegni_TemaNatale());
        ArrayList<CasePlacide> casePlacideArrayList = buildInfoAstroSwiss.getCasePlacide(giornoOraPosizioneDTO);
        return temaNataleDescrizione(pianetiTransiti, casePlacideArrayList);
    }



    public String temaNataleDescrizione(ArrayList<PianetaPosizTransito> pianetiTransiti, ArrayList<CasePlacide> casePlacideArrayList) {

        Properties caseSignificato = appConfig.caseSignificato();
        Properties aspettiPianetiProperties = appConfig.aspettiPianeti();
        Properties pianetiCaseSignificatoProperties = appConfig.pianetiCaseSignificato();
        Properties pianetiOroscopoSignificatoProperties = appConfig.pianetiOroscopoSignificato();
        Properties pianetaRetrogradoProperties = appConfig.pianetaRetrogrado();
        Properties segniZodiacaliProperties = appConfig.segniZodiacali();
        assegnaCaseAiPianeti(pianetiTransiti, casePlacideArrayList);
        ArrayList<Aspetti> aspetti = CalcoloAspetti.verificaAspetti(pianetiTransiti, appConfig.aspettiPianeti());

        //for(Aspetti var: aspetti) {
        //    logger.info( var.getNomePianeta_1() + " e "+ var.getNomePianeta_2() + " sono in "+ Constants.Aspetti.fromCode(var.getTipoAspetto()).getName() );
        //}

        //Sole: Indica l'ego, l'identità e il percorso di vita. Il segno zodiacale in cui si trova il Sole è quello comunemente noto come "segno zodiacale" di una persona.
        //Luna: Rappresenta le emozioni, i bisogni emotivi e l'inconscio. Il segno in cui si trova la Luna riflette come una persona vive e esprime le proprie emozioni.
        //Ascendente: È il segno che sorge all'orizzonte orientale al momento della nascita. Rappresenta l'immagine esterna, la prima impressione che si dà agli altri e il modo in cui si affronta la vita.

        // Se non sono presenti pianeti nella prima casa bisognerà tenere presente del segno che occupa la casa e dei pianeti domiciliati in quel segno per l'interpretazione.
        // Quindi nel prompt mostrare i segni coi suoi pianeti domiciliati

        StringBuilder descTemaNatale = new StringBuilder();
        descTemaNatale.append("<p><b>"+pianetiTransiti.get(0).descrizionePianeta()+"</b><br>");
        descTemaNatale.append(segnoZodiacale.getSegnoZodiacale( pianetiTransiti.get(0).getNumeroSegnoZodiacale() ).getDescrizioneMin()+"</p>");

        descTemaNatale.append("<p><b>"+pianetiTransiti.get(1).descrizionePianeta()+"</b></br>");
        descTemaNatale.append( segnoZodiacale.getSegnoZodiacale( pianetiTransiti.get(1).getNumeroSegnoZodiacale() ).getDescrizioneMin()+"</p>");

        descTemaNatale.append("<p><b>Ascendente in "+casePlacideArrayList.get(0).getNomeSegnoZodiacale()+"</b><br>");
        descTemaNatale.append( segnoZodiacale.getSegnoZodiacale( casePlacideArrayList.get(0).getNumeroSegnoZodiacale() ).getDescrizioneMin()+"</p>");


        descTemaNatale.append("<h4 class=\"mt-5\">Case</h4>"+ "");
        for (CasePlacide varCasa : casePlacideArrayList) {
            descTemaNatale.append("<b>" + varCasa.descrizioneCasaGradiCasaMinutiCasa() + (varCasa.getNomeCasa().equals("1") ? " (Ascendente)" : "") +"</b>");
            descTemaNatale.append("<ul>");
            descTemaNatale.append("<li>" + caseSignificato.getProperty(String.valueOf(varCasa.getNumeroCasa())) + "</li>");
            boolean pianetaPresete = false;
            for (PianetaPosizTransito varPianeta : pianetiTransiti) {
                if(varPianeta.getNomeCasa().equals(varCasa.getNomeCasa())){
                    pianetaPresete = true;
                    descTemaNatale.append("<li>" + varPianeta.descrizione_Pianeta_Segno_Gradi_Retrogrado_Casa() +" "+
                            pianetiCaseSignificatoProperties.getProperty(varPianeta.getNumeroPianeta()+"_"+varCasa.getNumeroCasa()) + "</li>");
                }
            }
            if( !pianetaPresete ){
                int[] pianetiSignori = segnoZodiacale.getSegnoZodiacale( varCasa.getNumeroSegnoZodiacale() ).getPianetiSignoreDelSegno();
                for (int pianetaSign : pianetiSignori) {
                    for (PianetaPosizTransito varPianeta : pianetiTransiti) {
                        if(varPianeta.getNumeroPianeta() == pianetaSign ){
                            descTemaNatale.append("<li>"+varPianeta.descrizione_Pianeta_Retrogrado()+"<i>"+" "+BuildInfoAstrologiaAstroSeek.pianetaDomicioSegnoCasa
                                +" "+"</i>"+ pianetiCaseSignificatoProperties.getProperty(varPianeta.getNumeroPianeta()+"_"+varCasa.getNumeroCasa()) + "</li>");
                        }
                    }
                }
            }
            descTemaNatale.append("</ul>");
        }


        descTemaNatale.append("<h4 class=\"mt-5\">Transiti dei Pianeti</h4>");
        int size = pianetiTransiti.size(); int count = 0;
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
                    var.getNumeroPianeta() == Constants.Pianeti.fromNumero(9).getNumero()) {
                descTemaNatale.append( var.descrizione_Pianeta_Gradi_Retrogrado_SignificatoPianetaSegno());
                if (count < size - 1) { descTemaNatale.append("<br>"); }
            }
        }



        List<Integer> aspettiPresenti = new ArrayList<>();
        if (!aspetti.isEmpty()) {
            descTemaNatale.append("<h4 class=\"mt-5\">Aspetti</h4>");
            size = aspetti.size(); count = 0;
            for (Aspetti var : aspetti) {
                descTemaNatale.append( var.getNomePianeta_1() + " e " + var.getNomePianeta_2() + " sono in " + Constants.Aspetti.fromCode(var.getTipoAspetto()).getName());
                if (count < size - 1) { descTemaNatale.append("<br>"); }
                aspettiPresenti.add(var.getTipoAspetto());
            }
        }



        descTemaNatale.append("<h4 class=\"mt-5\">Significato dei Segni</h4>");
        List<Constants.SegniZodiacali> segniZodiacaliList = Constants.SegniZodiacali.getAllSegniZodiacali();
        size = segniZodiacaliList.size();
        for (int i = 0; i < size; i++) {
            Constants.SegniZodiacali segno = segniZodiacaliList.get(i);
            descTemaNatale.append(  segno.getNome() +": "+ segniZodiacaliProperties.getProperty(String.valueOf(segno.getNumero())+"_min") );
            if (i < size - 1) { descTemaNatale.append("<br>"); }
        }



        descTemaNatale.append("<h4 class=\"mt-5\">Significato dei Pianeti</h4>");
        List<Constants.Pianeti> pianetiList = Constants.Pianeti.getAllPianeti();
        size = pianetiList.size();
        for (int i = 0; i < size; i++) {
            Constants.Pianeti pianeta = pianetiList.get(i);
            descTemaNatale.append(  pianeta.getNome() +": "+ pianetiOroscopoSignificatoProperties.getProperty( String.valueOf(pianeta.getNumero())+"_min") );
            if (i < size - 1) { descTemaNatale.append("<br>"); }
        }



        descTemaNatale.append("<h4 class=\"mt-5\">Significato Pianeta Retrogrado</h4>");
        descTemaNatale.append(pianetaRetrogradoProperties.getProperty( String.valueOf(0) ));



        descTemaNatale.append("<h4 class=\"mt-5\">Significato degli Aspetti</h4>");
        List<Constants.Aspetti> aspettiList = Constants.Aspetti.getAllAspetti();
        size = aspettiList.size();
        for (int i = 0; i < size; i++) {
            Constants.Aspetti aspetto = aspettiList.get(i);
            descTemaNatale.append(  aspetto.getName() +": "+ aspettiPianetiProperties.getProperty(String.valueOf(aspetto.getCode())+"_min") );
            if (i < size - 1) { descTemaNatale.append("<br>"); }
        }





        return descTemaNatale.toString();
    }




    public static void assegnaCaseAiPianeti(List<PianetaPosizTransito> pianetiTransiti, List<CasePlacide> casePlacideArrayList) {
        // Creare una copia della lista delle case per ordinarla
        List<CasePlacide> caseOrdinate = new ArrayList<>(casePlacideArrayList);
        caseOrdinate.sort(Comparator.comparingDouble(CasePlacide::getGradi));

        for (PianetaPosizTransito pianeta : pianetiTransiti) {
            double gradiPianeta = pianeta.getGradi();
            for (int i = 0; i < caseOrdinate.size(); i++) {
                CasePlacide casaCorrente = caseOrdinate.get(i);
                CasePlacide casaSuccessiva = caseOrdinate.get((i + 1) % caseOrdinate.size());

                // Controllare se il pianeta è tra la casa corrente e la successiva
                if (casaCorrente.getGradi() < casaSuccessiva.getGradi()) {
                    if (gradiPianeta >= casaCorrente.getGradi() && gradiPianeta < casaSuccessiva.getGradi()) {
                        pianeta.setNomeCasa(casaCorrente.getNomeCasa());
                        break;
                    }
                } else {
                    // Caso particolare in cui i gradi attraversano il punto zero
                    if (gradiPianeta >= casaCorrente.getGradi() || gradiPianeta < casaSuccessiva.getGradi()) {
                        pianeta.setNomeCasa(casaCorrente.getNomeCasa());
                        break;
                    }
                }
            }
        }
    }


}

