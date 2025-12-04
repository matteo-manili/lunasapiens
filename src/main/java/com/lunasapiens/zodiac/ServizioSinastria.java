package com.lunasapiens.zodiac;

import com.lunasapiens.Constants;
import com.lunasapiens.aiModels.openai.OpenAIGptTheokanning;
import com.lunasapiens.config.AppConfig;
import com.lunasapiens.config.PropertiesConfig;
import com.lunasapiens.dto.CoordinateDTO;
import com.lunasapiens.dto.GiornoOraPosizioneDTO;
import com.theokanning.openai.completion.chat.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Component
public class ServizioSinastria {

    private static final Logger logger = LoggerFactory.getLogger(ServizioSinastria.class);

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private PropertiesConfig propertiesConfig;

    @Autowired
    SegnoZodiacale segnoZodiacale;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CacheManager cacheManager;


    // tokensRisposta signfiica i token da aggiungere oltre i token per la domanda
    private final Double temperature = 0.0; private final Integer tokensAggiuntiPerRisposta = 0;
        private final Double caratteriPerTokenStima = 20.0;



    public StringBuilder chatBotSinastria( List<ChatMessage> chatMessageList ) {
        OpenAIGptTheokanning openAIGptTheokanning = new OpenAIGptTheokanning();
        return openAIGptTheokanning.eseguiOpenAIGptTheokanning(appConfig.getParamOpenAi().getApiKeyOpenAI(), temperature, tokensAggiuntiPerRisposta, caratteriPerTokenStima,
                 appConfig.getParamOpenAi().getModelGpt4_Mini() /* appConfig.getParamOpenAi().getModelGpt3_5()*/, chatMessageList );
    }



    public StringBuilder sinastriaDescrizione_AstrologiaAstroSeek(GiornoOraPosizioneDTO giornoOraPosizioneDTO, CoordinateDTO coordinateDTO) {
        BuildInfoAstrologiaAstroSeek buildInfoAstrologiaAstroSeek = new BuildInfoAstrologiaAstroSeek();
        BuildInfoAstrologiaAstroSeek result = buildInfoAstrologiaAstroSeek.catturaTemaNataleAstroSeek(restTemplate,
                cacheManager.getCache(Constants.URLS_ASTRO_SEEK_CACHE), giornoOraPosizioneDTO, coordinateDTO,
                propertiesConfig.transitiPianetiSegni_TemaNatale() );
        StringBuilder sinastriaDesc = sinastriaDescrizione(result.getPianetiPosizTransitoList(), result.getCasePlacidesList());
        return sinastriaDesc;
    }


    public StringBuilder sinastriaDescrizione_AstrologiaSwiss(GiornoOraPosizioneDTO giornoOraPosizioneDTO) {
        BuildInfoAstrologiaSwiss buildInfoAstroSwiss = new BuildInfoAstrologiaSwiss();
        ArrayList<Pianeti> pianetiList = buildInfoAstroSwiss.getPianetiTransiti(giornoOraPosizioneDTO, propertiesConfig.transitiPianetiSegni_TemaNatale());
        ArrayList<CasePlacide> casePlacideArrayList = buildInfoAstroSwiss.getCasePlacide(giornoOraPosizioneDTO);
        return sinastriaDescrizione(pianetiList, casePlacideArrayList);
    }



    public StringBuilder sinastriaDescrizione(List<Pianeti> pianetiTransiti, List<CasePlacide> casePlacideArrayList) {
        Properties caseSignificato = propertiesConfig.caseSignificato();
        Properties pianetiCaseSignificatoProperties = propertiesConfig.pianetiCaseSignificato();
        Properties segniAscendenteProperties = propertiesConfig.segniAscendente();
        Properties lunaSegniProperties = propertiesConfig.lunaSegni();

        ZodiacUtils.assegnaCaseAiPianeti(pianetiTransiti, casePlacideArrayList);
        ArrayList<Aspetti> aspetti = CalcoloAspetti.aspettiListPinaneti(pianetiTransiti, propertiesConfig.aspettiSignificato());

        //for(Aspetti var: aspetti) {
        //    logger.info( var.getNomePianeta_1() + " e "+ var.getNomePianeta_2() + " sono in "+ Constants.Aspetti.fromCode(var.getTipoAspetto()).getName() );
        //}

        //Sole: Indica l'ego, l'identità e il percorso di vita. Il segno zodiacale in cui si trova il Sole è quello comunemente noto come "segno zodiacale" di una persona.
        //Luna: Rappresenta le emozioni, i bisogni emotivi e l'inconscio. Il segno in cui si trova la Luna riflette come una persona vive e esprime le proprie emozioni.
        //Ascendente: È il segno che sorge all'orizzonte orientale al momento della nascita. Rappresenta l'immagine esterna, la prima impressione che si dà agli altri e il modo in cui si affronta la vita.

        // Se non sono presenti pianeti nella prima casa bisognerà tenere presente del segno che occupa la casa e dei pianeti domiciliati in quel segno per l'interpretazione.
        // Quindi nel prompt mostrare i segni coi suoi pianeti domiciliati

        StringBuilder descSinastria = new StringBuilder();
        SegnoZodiacale segnoSole = segnoZodiacale.getSegnoZodiacale( pianetiTransiti.get(0).getNumeroSegnoZodiacale() );
        SegnoZodiacale segnoLuna = segnoZodiacale.getSegnoZodiacale( pianetiTransiti.get(1).getNumeroSegnoZodiacale() );
        SegnoZodiacale segnoAscendente = segnoZodiacale.getSegnoZodiacale( casePlacideArrayList.get(0).getNumeroSegnoZodiacale() );

        descSinastria.append("<p><b>- "+pianetiTransiti.get(0).descrizionePianetaSegno()+"</b><br>");
        descSinastria.append(segnoSole.getDescrizioneMin()+"</p>");

        descSinastria.append("<p><b>- "+pianetiTransiti.get(1).descrizionePianetaSegno()+"</b></br>");
        descSinastria.append( lunaSegniProperties.getProperty(String.valueOf(segnoLuna.getNumeroSegnoZodiacale())) +"</p>");

        descSinastria.append("<p><b>- Ascendente in "+segnoAscendente.getNomeSegnoZodiacale()+"</b><br>");
        descSinastria.append( segniAscendenteProperties.getProperty(String.valueOf(segnoSole.getNumeroSegnoZodiacale())+"_"+segnoAscendente.getElemento().getCode())+"</p>");

        descSinastria.append("<h4 class=\"mt-5 mb-0\">Case</h4><br>");
        for (CasePlacide varCasa : casePlacideArrayList) {
            descSinastria.append("<b>- " + varCasa.descrizioneCasaGradiCasaMinutiCasa() +"</b>");
            descSinastria.append("<ul>");
            descSinastria.append("<li>Desc. Casa: "+caseSignificato.getProperty(String.valueOf(varCasa.getNumeroCasa())) + "</li>");
            boolean pianetaPresete = false;
            for (Pianeti varPianeta : pianetiTransiti) {
                if(varPianeta.getNomeCasa().equals(varCasa.getNomeCasa())){
                    pianetaPresete = true;
                    descSinastria.append("<li>Pianeta nella casa: "+varPianeta.descrizione_Pianeta_Segno_Gradi_Retrogrado_Casa() +" "+
                            pianetiCaseSignificatoProperties.getProperty(varPianeta.getNumeroPianeta()+"_"+varCasa.getNumeroCasa()) + "</li>");
                }
            }
            if( !pianetaPresete ){
                int[] pianetiSignori = segnoZodiacale.getSegnoZodiacale( varCasa.getNumeroSegnoZodiacale() ).getPianetiSignoreDelSegno();
                for (int pianetaSign : pianetiSignori) {
                    for (Pianeti varPianeta : pianetiTransiti) {
                        if(varPianeta.getNumeroPianeta() == pianetaSign ){
                            descSinastria.append("<li>Pianeta nella casa: "+varPianeta.descrizione_Pianeta_Retrogrado()+"<i>"+" "+BuildInfoAstrologiaAstroSeek.pianetaDomicioSegnoCasa
                                +" "+"</i>"+ pianetiCaseSignificatoProperties.getProperty(varPianeta.getNumeroPianeta()+"_"+varCasa.getNumeroCasa()) + "</li>");
                        }
                    }
                }
            }
            descSinastria.append("</ul>");
        }

        int size = pianetiTransiti.size(); int count = 0;
        descSinastria.append("<h4 class=\"mt-5 mb-0\">Transiti dei Pianeti</h4><br>");
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
                    var.getNumeroPianeta() == Constants.Pianeti.fromNumero(9).getNumero()) {
                descSinastria.append("- "+var.descrizione_Pianeta_Gradi_Retrogrado_SignificatoPianetaSegno());
                if (count < size - 1) { descSinastria.append("<br>"); }
            }
        }

        if (!aspetti.isEmpty()) {
            descSinastria.append("<h4 class=\"mt-5 mb-0\">Aspetti</h4><br>");
            size = aspetti.size(); count = 0;
            for (Aspetti var : aspetti) {
                descSinastria.append("- "+var.getNomePianeta_1() + " e " + var.getNomePianeta_2() + " sono in " + Constants.Aspetti.fromCode(var.getTipoAspetto()).getName());
                if (count < size - 1) { descSinastria.append("<br>"); }
            }
        }

        return descSinastria;
    }



    public StringBuilder significatiSinastriaDescrizione() {
        Properties aspettiPianetiProperties = propertiesConfig.aspettiSignificato();
        Properties pianetiOroscopoSignificatoProperties = propertiesConfig.pianetiOroscopoSignificato();
        Properties pianetaRetrogradoProperties = propertiesConfig.pianetaRetrogrado();
        Properties segniZodProperties = propertiesConfig.segniZodiacali();

        StringBuilder significatiSinastria = new StringBuilder();

        significatiSinastria.append("<h4 class=\"mt-5 mb-0\">Significato dei Segni</h4><br>");
        List<Constants.SegniZodiacali> segniZodiacaliList = Constants.SegniZodiacali.getAllSegniZodiacali();
        int size = segniZodiacaliList.size();
        for (int i = 0; i < size; i++) {
            Constants.SegniZodiacali segno = segniZodiacaliList.get(i);
            significatiSinastria.append("- "+segno.getNome() +": "+ segniZodProperties.getProperty(String.valueOf(segno.getNumero())+"_min") );
            if (i < size - 1) { significatiSinastria.append("<br>"); }
        }


        significatiSinastria.append("<h4 class=\"mt-5 mb-0\">Significato dei Pianeti</h4><br>");
        List<Constants.Pianeti> pianetiList = Constants.Pianeti.getAllPianetiNormali();
        size = pianetiList.size();
        for (int i = 0; i < size; i++) {
            Constants.Pianeti pianeta = pianetiList.get(i);
            significatiSinastria.append("- "+pianeta.getNome() +": "+ pianetiOroscopoSignificatoProperties.getProperty( String.valueOf(pianeta.getNumero())+"_min") );
            if (i < size - 1) { significatiSinastria.append("<br>"); }
        }


        significatiSinastria.append("<h4 class=\"mt-5 mb-0\">Significato Pianeta Retrogrado</h4><br>");
        significatiSinastria.append("- "+pianetaRetrogradoProperties.getProperty( String.valueOf(0) ));

        significatiSinastria.append("<h4 class=\"mt-5 mb-0\">Significato degli Aspetti</h4><br>");
        List<Constants.Aspetti> aspettiList = Constants.Aspetti.getAllAspetti();
        size = aspettiList.size();
        for (int i = 0; i < size; i++) {
            Constants.Aspetti aspetto = aspettiList.get(i);
            significatiSinastria.append("- "+aspetto.getName() +": "+ aspettiPianetiProperties.getProperty(String.valueOf(aspetto.getCode())+"_min") );
            if (i < size - 1) { significatiSinastria.append("<br>"); }
        }

        return significatiSinastria;
    }







}

