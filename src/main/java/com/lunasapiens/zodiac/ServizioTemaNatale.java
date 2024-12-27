package com.lunasapiens.zodiac;

import com.lunasapiens.config.AppConfig;
import com.lunasapiens.Constants;
import com.lunasapiens.config.PropertiesConfig;
import com.lunasapiens.dto.AstroChartDTO;
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



    public StringBuilder chatBotTemaNatale( List<ChatMessage> chatMessageList ) {
        OpenAIGptTheokanning openAIGptTheokanning = new OpenAIGptTheokanning();
        return openAIGptTheokanning.eseguiOpenAIGptTheokanning(appConfig.getParamOpenAi().getApiKeyOpenAI(), temperature, tokensAggiuntiPerRisposta, caratteriPerTokenStima,
                 appConfig.getParamOpenAi().getModelGpt4_Mini() /* appConfig.getParamOpenAi().getModelGpt3_5()*/, chatMessageList );
    }



    public StringBuilder temaNataleDescrizione_AstrologiaAstroSeek(GiornoOraPosizioneDTO giornoOraPosizioneDTO, CoordinateDTO coordinateDTO) {
        BuildInfoAstrologiaAstroSeek buildInfoAstrologiaAstroSeek = new BuildInfoAstrologiaAstroSeek();
        BuildInfoAstrologiaAstroSeek result = buildInfoAstrologiaAstroSeek.catturaTemaNataleAstroSeek(restTemplate,
                cacheManager.getCache(Constants.URLS_ASTRO_SEEK_CACHE), giornoOraPosizioneDTO, coordinateDTO,
                propertiesConfig.transitiPianetiSegni_TemaNatale() );
        StringBuilder temaNataleDesc = temaNataleDescrizione(result.getPianetiPosizTransitoList(), result.getCasePlacidesList());
        return temaNataleDesc;
    }


    public AstroChartDTO astroChart_AstrologiaAstroSeek(GiornoOraPosizioneDTO giornoOraPosizioneDTO, CoordinateDTO coordinateDTO) {
        BuildInfoAstrologiaAstroSeek buildInfoAstrologiaAstroSeek = new BuildInfoAstrologiaAstroSeek();
        BuildInfoAstrologiaAstroSeek result = buildInfoAstrologiaAstroSeek.catturaTemaNataleAstroSeek(restTemplate,
                cacheManager.getCache(Constants.URLS_ASTRO_SEEK_CACHE), giornoOraPosizioneDTO, coordinateDTO,
                propertiesConfig.transitiPianetiSegni_TemaNatale() );

        Map<String, Object[]> planets = new HashMap<>();
        for(Pianeti pianetaIte: result.getPianetiPosizTransitoList() ){
            if( pianetaIte.getNumeroPianeta() == Constants.Pianeti.SOLE.getNumero() ){
                planets.put(Constants.Pianeti.SOLE.getNomeAstroChart(), gradiPianetaAstroChart(pianetaIte.isRetrogrado(),pianetaIte.getGradi()));
            }
            if( pianetaIte.getNumeroPianeta() == Constants.Pianeti.LUNA.getNumero() ){
                planets.put(Constants.Pianeti.LUNA.getNomeAstroChart(), gradiPianetaAstroChart(pianetaIte.isRetrogrado(),pianetaIte.getGradi()));
            }
            if( pianetaIte.getNumeroPianeta() == Constants.Pianeti.MERCURIO.getNumero() ){
                planets.put(Constants.Pianeti.MERCURIO.getNomeAstroChart(), gradiPianetaAstroChart(pianetaIte.isRetrogrado(),pianetaIte.getGradi()));
            }
            if( pianetaIte.getNumeroPianeta() == Constants.Pianeti.VENERE.getNumero() ){
                planets.put(Constants.Pianeti.VENERE.getNomeAstroChart(), gradiPianetaAstroChart(pianetaIte.isRetrogrado(),pianetaIte.getGradi()));
            }
            if( pianetaIte.getNumeroPianeta() == Constants.Pianeti.MARTE.getNumero() ){
                planets.put(Constants.Pianeti.MARTE.getNomeAstroChart(), gradiPianetaAstroChart(pianetaIte.isRetrogrado(),pianetaIte.getGradi()));
            }
            if( pianetaIte.getNumeroPianeta() == Constants.Pianeti.GIOVE.getNumero() ){
                planets.put(Constants.Pianeti.GIOVE.getNomeAstroChart(), gradiPianetaAstroChart(pianetaIte.isRetrogrado(),pianetaIte.getGradi()));
            }
            if( pianetaIte.getNumeroPianeta() == Constants.Pianeti.SATURNO.getNumero() ){
                planets.put(Constants.Pianeti.SATURNO.getNomeAstroChart(), gradiPianetaAstroChart(pianetaIte.isRetrogrado(),pianetaIte.getGradi()));
            }
            if( pianetaIte.getNumeroPianeta() == Constants.Pianeti.URANO.getNumero() ){
                planets.put(Constants.Pianeti.URANO.getNomeAstroChart(), gradiPianetaAstroChart(pianetaIte.isRetrogrado(),pianetaIte.getGradi()));
            }
            if( pianetaIte.getNumeroPianeta() == Constants.Pianeti.NETTUNO.getNumero() ){
                planets.put(Constants.Pianeti.NETTUNO.getNomeAstroChart(), gradiPianetaAstroChart(pianetaIte.isRetrogrado(),pianetaIte.getGradi()));
            }
            if( pianetaIte.getNumeroPianeta() == Constants.Pianeti.PLUTONE.getNumero() ){
                planets.put(Constants.Pianeti.PLUTONE.getNomeAstroChart(), gradiPianetaAstroChart(pianetaIte.isRetrogrado(),pianetaIte.getGradi()));
            }
            if( pianetaIte.getNumeroPianeta() == Constants.Pianeti.NODE_M.getNumero() ){
                planets.put(Constants.Pianeti.NODE_M.getNomeAstroChart(), gradiPianetaAstroChart(pianetaIte.isRetrogrado(),pianetaIte.getGradi()));
            }
            if( pianetaIte.getNumeroPianeta() == Constants.Pianeti.NODE_S.getNumero() ){
                planets.put(Constants.Pianeti.NODE_S.getNomeAstroChart(), gradiPianetaAstroChart(pianetaIte.isRetrogrado(),pianetaIte.getGradi()));
            }
            if( pianetaIte.getNumeroPianeta() == Constants.Pianeti.LILITH.getNumero() ){
                planets.put(Constants.Pianeti.LILITH.getNomeAstroChart(), gradiPianetaAstroChart(pianetaIte.isRetrogrado(),pianetaIte.getGradi()));
            }
            if( pianetaIte.getNumeroPianeta() == Constants.Pianeti.CHIRON.getNumero() ){
                planets.put(Constants.Pianeti.CHIRON.getNomeAstroChart(), gradiPianetaAstroChart(pianetaIte.isRetrogrado(),pianetaIte.getGradi()));
            }
        }


        // Estrai i valori di gradi in una nuova lista
        List<Integer> cusps = new ArrayList<>();
        for (CasePlacide casePlacide : result.getCasePlacidesList()) {
            cusps.add( (int)casePlacide.getGradi() );
        }

        return new AstroChartDTO(planets, cusps);
    }

    private Object[] gradiPianetaAstroChart(boolean retrogrado, double gradi){
        long rounded = Math.round(gradi);
        if (retrogrado){
            return new Object[]{rounded, -0.2};
        }else{
            return new Object[]{rounded};
        }
    }


    public StringBuilder temaNataleDescrizione_AstrologiaSwiss(GiornoOraPosizioneDTO giornoOraPosizioneDTO) {
        BuildInfoAstrologiaSwiss buildInfoAstroSwiss = new BuildInfoAstrologiaSwiss();
        ArrayList<Pianeti> pianetiList = buildInfoAstroSwiss.getPianetiTransiti(giornoOraPosizioneDTO, propertiesConfig.transitiPianetiSegni_TemaNatale());
        ArrayList<CasePlacide> casePlacideArrayList = buildInfoAstroSwiss.getCasePlacide(giornoOraPosizioneDTO);
        return temaNataleDescrizione(pianetiList, casePlacideArrayList);
    }



    public StringBuilder temaNataleDescrizione(List<Pianeti> pianetiTransiti, List<CasePlacide> casePlacideArrayList) {
        Properties caseSignificato = propertiesConfig.caseSignificato();
        Properties pianetiCaseSignificatoProperties = propertiesConfig.pianetiCaseSignificato();
        Properties segniAscendenteProperties = propertiesConfig.segniAscendente();
        Properties lunaSegniProperties = propertiesConfig.lunaSegni(); // non lo uso....

        Properties transitiPianetiSegniTMProperties = propertiesConfig.transitiPianetiSegni_TemaNatale();


        ZodiacUtils.assegnaCaseAiPianeti(pianetiTransiti, casePlacideArrayList);
        ArrayList<Aspetti> aspetti = CalcoloAspetti.aspettiListPinaneti(pianetiTransiti, propertiesConfig.aspettiPianeti());

        //for(Aspetti var: aspetti) {
        //    logger.info( var.getNomePianeta_1() + " e "+ var.getNomePianeta_2() + " sono in "+ Constants.Aspetti.fromCode(var.getTipoAspetto()).getName() );
        //}

        //Sole: Indica l'ego, l'identità e il percorso di vita. Il segno zodiacale in cui si trova il Sole è quello comunemente noto come "segno zodiacale" di una persona.
        //Luna: Rappresenta le emozioni, i bisogni emotivi e l'inconscio. Il segno in cui si trova la Luna riflette come una persona vive e esprime le proprie emozioni.
        //Ascendente: È il segno che sorge all'orizzonte orientale al momento della nascita. Rappresenta l'immagine esterna, la prima impressione che si dà agli altri e il modo in cui si affronta la vita.

        // Se non sono presenti pianeti nella prima casa bisognerà tenere presente del segno che occupa la casa e dei pianeti domiciliati in quel segno per l'interpretazione.
        // Quindi nel prompt mostrare i segni coi suoi pianeti domiciliati

        StringBuilder descTemaNatale = new StringBuilder();
        SegnoZodiacale segnoSole = segnoZodiacale.getSegnoZodiacale( pianetiTransiti.get(0).getNumeroSegnoZodiacale() );
        SegnoZodiacale segnoLuna = segnoZodiacale.getSegnoZodiacale( pianetiTransiti.get(1).getNumeroSegnoZodiacale() );
        SegnoZodiacale segnoAscendente = segnoZodiacale.getSegnoZodiacale( casePlacideArrayList.get(0).getNumeroSegnoZodiacale() );

        descTemaNatale.append("<p><b>- "+pianetiTransiti.get(0).descrizionePianetaSegno()+"</b><br>");
        descTemaNatale.append(segnoSole.getDescrizioneMin()+"</p>");

        descTemaNatale.append("<p><b>- "+pianetiTransiti.get(1).descrizionePianetaSegno()+"</b></br>");
        descTemaNatale.append( transitiPianetiSegniTMProperties.getProperty( "1_"+String.valueOf(segnoLuna.getNumeroSegnoZodiacale())) +"</p>");

        descTemaNatale.append("<p><b>- Ascendente in "+segnoAscendente.getNomeSegnoZodiacale()+"</b><br>");
        descTemaNatale.append( segniAscendenteProperties.getProperty(String.valueOf(segnoSole.getNumeroSegnoZodiacale())+"_"+segnoAscendente.getElemento().getCode())+"</p>");

        descTemaNatale.append("<h4 class=\"mt-5 mb-0\">Case</h4><br>");
        for (CasePlacide varCasa : casePlacideArrayList) {
            descTemaNatale.append("<b>- " + varCasa.descrizioneCasaGradiCasaMinutiCasa() +"</b>");
            descTemaNatale.append("<ul>");
            descTemaNatale.append("<li>Desc. Casa: "+caseSignificato.getProperty(String.valueOf(varCasa.getNumeroCasa())) + "</li>");
            boolean pianetaPresete = false;
            for (Pianeti varPianeta : pianetiTransiti) {
                if(varPianeta.getNomeCasa().equals(varCasa.getNomeCasa())){
                    pianetaPresete = true;
                    descTemaNatale.append("<li>Pianeta nella casa: "+varPianeta.descrizione_Pianeta_Segno_Gradi_Retrogrado_Casa() +" "+
                            pianetiCaseSignificatoProperties.getProperty(varPianeta.getNumeroPianeta()+"_"+varCasa.getNumeroCasa()) + "</li>");
                }
            }
            if( !pianetaPresete ){
                int[] pianetiSignori = segnoZodiacale.getSegnoZodiacale( varCasa.getNumeroSegnoZodiacale() ).getPianetiSignoreDelSegno();
                for (int pianetaSign : pianetiSignori) {
                    for (Pianeti varPianeta : pianetiTransiti) {
                        if(varPianeta.getNumeroPianeta() == pianetaSign ){
                            descTemaNatale.append("<li>Pianeta nella casa: "+varPianeta.descrizione_Pianeta_Retrogrado()+"<i>"+" "+BuildInfoAstrologiaAstroSeek.pianetaDomicioSegnoCasa
                                +" "+"</i>"+ pianetiCaseSignificatoProperties.getProperty(varPianeta.getNumeroPianeta()+"_"+varCasa.getNumeroCasa()) + "</li>");
                        }
                    }
                }
            }
            descTemaNatale.append("</ul>");
        }

        int size = pianetiTransiti.size(); int count = 0;
        descTemaNatale.append("<h4 class=\"mt-5 mb-0\">Transiti dei Pianeti</h4><br>");
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
                descTemaNatale.append("- "+var.descrizione_Pianeta_Gradi_Retrogrado_SignificatoPianetaSegno());
                if (count < size - 1) { descTemaNatale.append("<br>"); }
            }
        }

        if (!aspetti.isEmpty()) {
            descTemaNatale.append("<h4 class=\"mt-5 mb-0\">Aspetti</h4><br>");
            size = aspetti.size(); count = 0;
            for (Aspetti var : aspetti) {
                descTemaNatale.append("- "+var.getNomePianeta_1() + " e " + var.getNomePianeta_2() + " sono in " + Constants.Aspetti.fromCode(var.getTipoAspetto()).getName());
                if (count < size - 1) { descTemaNatale.append("<br>"); }
            }
        }


        return descTemaNatale;
    }



    public StringBuilder significatiTemaNataleDescrizione() {
        Properties aspettiPianetiProperties = propertiesConfig.aspettiPianeti();
        Properties pianetiOroscopoSignificatoProperties = propertiesConfig.pianetiOroscopoSignificato();
        Properties pianetaRetrogradoProperties = propertiesConfig.pianetaRetrogrado();
        Properties segniZodProperties = propertiesConfig.segniZodiacali();

        StringBuilder significatiTemaNatale = new StringBuilder();

        significatiTemaNatale.append("<h4 class=\"mt-5 mb-0\">Significato dei Segni</h4><br>");
        List<Constants.SegniZodiacali> segniZodiacaliList = Constants.SegniZodiacali.getAllSegniZodiacali();
        int size = segniZodiacaliList.size();
        for (int i = 0; i < size; i++) {
            Constants.SegniZodiacali segno = segniZodiacaliList.get(i);
            significatiTemaNatale.append("- "+segno.getNome() +": "+ segniZodProperties.getProperty(String.valueOf(segno.getNumero())+"_min") );
            if (i < size - 1) { significatiTemaNatale.append("<br>"); }
        }


        significatiTemaNatale.append("<h4 class=\"mt-5 mb-0\">Significato dei Pianeti</h4><br>");
        List<Constants.Pianeti> pianetiList = Constants.Pianeti.getAllPianetiNormali();
        size = pianetiList.size();
        for (int i = 0; i < size; i++) {
            Constants.Pianeti pianeta = pianetiList.get(i);
            significatiTemaNatale.append("- "+pianeta.getNome() +": "+ pianetiOroscopoSignificatoProperties.getProperty( String.valueOf(pianeta.getNumero())+"_min") );
            if (i < size - 1) { significatiTemaNatale.append("<br>"); }
        }


        significatiTemaNatale.append("<h4 class=\"mt-5 mb-0\">Significato Pianeta Retrogrado</h4><br>");
        significatiTemaNatale.append("- "+pianetaRetrogradoProperties.getProperty( String.valueOf(0) ));

        significatiTemaNatale.append("<h4 class=\"mt-5 mb-0\">Significato degli Aspetti</h4><br>");
        List<Constants.Aspetti> aspettiList = Constants.Aspetti.getAllAspetti();
        size = aspettiList.size();
        for (int i = 0; i < size; i++) {
            Constants.Aspetti aspetto = aspettiList.get(i);
            significatiTemaNatale.append("- "+aspetto.getName() +": "+ aspettiPianetiProperties.getProperty(String.valueOf(aspetto.getCode())+"_min") );
            if (i < size - 1) { significatiTemaNatale.append("<br>"); }
        }

        return significatiTemaNatale;
    }







}

