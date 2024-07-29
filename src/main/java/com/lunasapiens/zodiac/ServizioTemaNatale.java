package com.lunasapiens.zodiac;

import com.lunasapiens.config.AppConfig;
import com.lunasapiens.Constants;
import com.lunasapiens.dto.GiornoOraPosizioneDTO;
import com.theokanning.openai.completion.chat.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

@Component
public class ServizioTemaNatale {

    private static final Logger logger = LoggerFactory.getLogger(ServizioTemaNatale.class);

    @Autowired
    private AppConfig appConfig;

    @Autowired
    SegnoZodiacale segnoZodiacale;



    // tokensRisposta signfiica i token da aggiungere oltre i token per la domanda
    private Double temperature = 0.4; private Integer tokensRisposta = 500;



    public StringBuilder chatBotTemaNatale( List<ChatMessage> chatMessageList ) {

        OpenAIGptTheokanning openAIGptTheokanning = new OpenAIGptTheokanning();
        return openAIGptTheokanning.eseguiOpenAIGptTheokanning(appConfig.getParamOpenAi().getApiKeyOpenAI(), temperature, tokensRisposta,
                appConfig.getParamOpenAi().getModelGpt4_Mini(), chatMessageList );


    }




    public String temaNataleDescrizione(GiornoOraPosizioneDTO giornoOraPosizioneDTO) {

        Properties caseSignificato = appConfig.caseSignificato();
        Properties aspettiPianetiProperties = appConfig.aspettiPianeti();
        Properties pianetiCaseSignificatoProperties = appConfig.pianetiCaseSignificato();

        BuildInfoAstrologiaSwiss buildInfoAstroSwiss = new BuildInfoAstrologiaSwiss();

        ArrayList<CasePlacide> casePlacideArrayList = buildInfoAstroSwiss.getCasePlacide(giornoOraPosizioneDTO);
        ArrayList<PianetaPosizTransito> pianetiTransiti = buildInfoAstroSwiss.getPianetiTransiti(giornoOraPosizioneDTO, appConfig.transitiPianetiSegni_TemaNatale());
        assegnaCaseAiPianeti(pianetiTransiti, casePlacideArrayList);
        ArrayList<Aspetti> aspetti = CalcoloAspetti.verificaAspetti(pianetiTransiti, appConfig.aspettiPianeti());

        for(Aspetti var: aspetti) {
            System.out.println( var.getNomePianeta_1() + " e "+ var.getNomePianeta_2() + " sono in "+ Constants.Aspetti.fromCode(var.getTipoAspetto()).getName() );
        }

        //Sole: Indica l'ego, l'identità e il percorso di vita. Il segno zodiacale in cui si trova il Sole è quello comunemente noto come "segno zodiacale" di una persona.
        //Luna: Rappresenta le emozioni, i bisogni emotivi e l'inconscio. Il segno in cui si trova la Luna riflette come una persona vive e esprime le proprie emozioni.
        //Ascendente: È il segno che sorge all'orizzonte orientale al momento della nascita. Rappresenta l'immagine esterna, la prima impressione che si dà agli altri e il modo in cui si affronta la vita.

        // Se non sono presenti pianeti nella prima casa bisognerà tenere presente del segno che occupa la casa e dei pianeti domiciliati in quel segno per l'interpretazione.
        // Quindi nel prompt mostrare i segni coi suoi pianeti domiciliati

        StringBuilder descTemaNatale = new StringBuilder();
        descTemaNatale.append("<big><p><b>"+pianetiTransiti.get(0).descrizionePianeta()+"</b><br>");
        descTemaNatale.append(segnoZodiacale.getSegnoZodiacale( pianetiTransiti.get(0).getNumeroSegnoZodiacale() ).getDescrizioneMin()+"</p>");

        descTemaNatale.append( "<p><b>"+pianetiTransiti.get(1).descrizionePianeta()+"</b></br>");
        descTemaNatale.append( segnoZodiacale.getSegnoZodiacale( pianetiTransiti.get(1).getNumeroSegnoZodiacale() ).getDescrizioneMin()+"</p>");

        descTemaNatale.append( "<p><b>Ascendente in "+casePlacideArrayList.get(0).getNomeSegnoZodiacale()+"</b><br>");
        descTemaNatale.append( segnoZodiacale.getSegnoZodiacale( casePlacideArrayList.get(0).getNumeroSegnoZodiacale() ).getDescrizioneMin()+"</p></big>");

        descTemaNatale.append( "<h4 class=\"mt-5\">Case</h4>"+ "<p>");
        for (CasePlacide varCasa : casePlacideArrayList) {
            descTemaNatale.append( "<b>" + varCasa.descrizioneCasaGradi() + (varCasa.getNomeCasa().equals("1") ? " (Ascendente)" : "") +"</b>");
            descTemaNatale.append( "<ul>");
            descTemaNatale.append( "<li>" + caseSignificato.getProperty(varCasa.getNomeCasa()) + "</li>");
            boolean pianetaPresete = false;
            for (PianetaPosizTransito varPianeta : pianetiTransiti) {
                if(varPianeta.getNomeCasa().equals(varCasa.getNomeCasa()) ){
                    pianetaPresete = true;
                    descTemaNatale.append( "<li>" + varPianeta.descrizione_Pianeta_Segno_Gradi_Retrogrado_Casa() +" "+
                            pianetiCaseSignificatoProperties.getProperty(varPianeta.getNumeroPianeta()+"_"+varCasa.getNomeCasa()) + "</li>");
                }
            }
            if( !pianetaPresete ){
                int[] pianetiSignori = segnoZodiacale.getSegnoZodiacale( varCasa.getNumeroSegnoZodiacale() ).getPianetiSignoreDelSegno();
                for (int pianetaSign : pianetiSignori) {
                    for (PianetaPosizTransito varPianeta : pianetiTransiti) {
                        if(varPianeta.getNumeroPianeta() == pianetaSign ){
                            descTemaNatale.append( "<li>"+varPianeta.descrizione_Pianeta_Retrogrado()+"<i> (Pianeta domicilio del segno della Casa) </i>" +
                                    pianetiCaseSignificatoProperties.getProperty(varPianeta.getNumeroPianeta()+"_"+varCasa.getNomeCasa()) + "</li>");
                        }
                    }
                }
            }
            descTemaNatale.append( "</ul>");
        }
        descTemaNatale.append( "</p>");


        descTemaNatale.append( "<h4 class=\"mt-5\">Transiti</h4>"+ "<p>");
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
                if (count < size - 1) { descTemaNatale.append( "<br>"); }
            }
        }
        descTemaNatale.append( "</p>");

        // ASPETTI
        List<Integer> aspettiPresenti = new ArrayList<>();
        if (!aspetti.isEmpty()) {
            descTemaNatale.append( "<h4 class=\"mt-5\">Aspetti</h4>"+ "<p>");
            size = aspetti.size(); count = 0;
            for (Aspetti var : aspetti) {
                descTemaNatale.append( var.getNomePianeta_1() + " e " + var.getNomePianeta_2() + " sono in " + Constants.Aspetti.fromCode(var.getTipoAspetto()).getName());
                if (count < size - 1) { descTemaNatale.append( "<br>"); }
                aspettiPresenti.add(var.getTipoAspetto());
            }
            descTemaNatale.append( "</p>");
        }

        if(aspetti != null && !aspetti.isEmpty()) {
            descTemaNatale.append( "<h4 class=\"mt-5\">Significato Aspetti</h4>"+ "<p>");
            size = Constants.Aspetti.values().length; count = 0;
            for (Constants.Aspetti aspettiConstants : Constants.Aspetti.values()) {
                if(aspettiPresenti.contains(aspettiConstants.getCode())) {
                    descTemaNatale.append( aspettiConstants.getName()+": "+aspettiPianetiProperties.getProperty( String.valueOf(aspettiConstants.getCode())+"_min"));
                    if (count < size - 1) { descTemaNatale.append( "<br>"); }
                }
            }
            descTemaNatale.append( "</p>");
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

