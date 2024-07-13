package com.lunasapiens.zodiac;

import com.lunasapiens.AppConfig;
import com.lunasapiens.Constants;
import com.lunasapiens.dto.GiornoOraPosizioneDTO;
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


    //private Double temperature = 0.5; private Integer maxTokens = 2500;



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




    public String temaNataleDescrizione(GiornoOraPosizioneDTO giornoOraPosizioneDTO) {

        Properties caseSignificato = appConfig.caseSignificato();
        Properties aspettiPianetiProperties = appConfig.aspettiPianeti();
        Properties pianetiCaseSignificatoProperties = appConfig.pianetiCaseSignificato();

        BuildInfoAstrologiaSwiss buildInfoAstroSwiss = new BuildInfoAstrologiaSwiss();

        ArrayList<CasePlacide> casePlacideArrayList = buildInfoAstroSwiss.getCasePlacide(giornoOraPosizioneDTO);
        ArrayList<PianetaPosizTransito> pianetiTransiti = buildInfoAstroSwiss.getPianetiTransiti(giornoOraPosizioneDTO, appConfig.transitiPianetiSegni_TemaNatale());
        assegnaCaseAiPianeti(pianetiTransiti, casePlacideArrayList);
        ArrayList<Aspetti> aspetti = CalcoloAspetti.verificaAspetti(pianetiTransiti, appConfig.aspettiPianeti());
        List<Integer> aspettiPresenti = new ArrayList<>();

        //Sole: Indica l'ego, l'identità e il percorso di vita. Il segno zodiacale in cui si trova il Sole è quello comunemente noto come "segno zodiacale" di una persona.
        //Luna: Rappresenta le emozioni, i bisogni emotivi e l'inconscio. Il segno in cui si trova la Luna riflette come una persona vive e esprime le proprie emozioni.
        //Ascendente: È il segno che sorge all'orizzonte orientale al momento della nascita. Rappresenta l'immagine esterna, la prima impressione che si dà agli altri e il modo in cui si affronta la vita.

        // Se non sono presenti pianeti nella prima casa bisognerà tenere presente del segno che occupa la casa e dei pianeti domiciliati in quel segno per l'interpretazione.
        // Quindi nel prompt mostrare i segni coi suoi pianeti domiciliati

        String descrizioneTemaNatale = "<big><p><b>"+pianetiTransiti.get(0).descrizionePianeta()+"</b><br>";
        descrizioneTemaNatale += segnoZodiacale.getSegnoZodiacale( pianetiTransiti.get(0).getNumeroSegnoZodiacale() ).getDescrizioneMin()+"</p>";

        descrizioneTemaNatale += "<p><b>"+pianetiTransiti.get(1).descrizionePianeta()+"</b></br>";
        descrizioneTemaNatale += segnoZodiacale.getSegnoZodiacale( pianetiTransiti.get(1).getNumeroSegnoZodiacale() ).getDescrizioneMin()+"</p>";

        descrizioneTemaNatale += "<p><b>Ascendente in "+casePlacideArrayList.get(0).getNomeSegnoZodiacale()+".</b><br>";
        descrizioneTemaNatale += segnoZodiacale.getSegnoZodiacale( casePlacideArrayList.get(0).getNumeroSegnoZodiacale() ).getDescrizioneMin()+"</p></big>";

        descrizioneTemaNatale += "<p>" + "<h4>Case:</h4>";
        for (CasePlacide varCasa : casePlacideArrayList) {
            descrizioneTemaNatale += "<br><b>" + varCasa.descrizioneCasaGradi() + "</b>";
            descrizioneTemaNatale += "<ul>";
            descrizioneTemaNatale += "<li>" + caseSignificato.getProperty(varCasa.getNomeCasa()) + "</li>";
            boolean pianetaPresete = false;
            for (PianetaPosizTransito varPianeta : pianetiTransiti) {
                if(varPianeta.getNomeCasa().equals(varCasa.getNomeCasa()) ){
                    pianetaPresete = true;
                    descrizioneTemaNatale += "<li>" + varPianeta.descrizione_Pianeta_Segno_Gradi_Retrogrado_Casa() +" "+
                            pianetiCaseSignificatoProperties.getProperty(varPianeta.getNumeroPianeta()+"_"+varCasa.getNomeCasa()) + "</li>";
                }
            }
            if( !pianetaPresete ){
                int[] pianetiSignori = segnoZodiacale.getSegnoZodiacale( varCasa.getNumeroSegnoZodiacale() ).getPianetiSignoreDelSegno();
                for (int pianetaSign : pianetiSignori) {
                    for (PianetaPosizTransito varPianeta : pianetiTransiti) {
                        if(varPianeta.getNumeroPianeta() == pianetaSign ){
                            descrizioneTemaNatale += "<li>"+varPianeta.descrizione_Pianeta_Retrogrado()+"<i> (Pianeta domicilio del segno della Casa) </i>" +
                                    pianetiCaseSignificatoProperties.getProperty(varPianeta.getNumeroPianeta()+"_"+varCasa.getNomeCasa()) + "</li>";
                        }
                    }
                }
            }
            descrizioneTemaNatale += "</ul>";
        }
        descrizioneTemaNatale += "</p>";


        descrizioneTemaNatale += "<p>" + "<h4>Transiti:</h4>";
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
                descrizioneTemaNatale += "<br>" + var.descrizione_Pianeta_Gradi_Retrogrado_Casa_SignificatoPianetaSegno();
            }
        }
        descrizioneTemaNatale += "</p>";

        // ASPETTI
        if (!aspetti.isEmpty()) {
            descrizioneTemaNatale += "<p>" + "<h4>Aspetti:</h4>";
            for (Aspetti var : aspetti) {
                descrizioneTemaNatale += "<br>" + var.getNomePianeta_1() + " e " + var.getNomePianeta_2() + " sono in " + Constants.Aspetti.fromCode(var.getTipoAspetto()).getName();
                aspettiPresenti.add(var.getTipoAspetto());
            }
        }
        descrizioneTemaNatale += "</p>";

        if(aspetti != null && !aspetti.isEmpty()){
            descrizioneTemaNatale += "<p>" + "<h4>Significato Aspetti:</h4>";
            for (Constants.Aspetti aspettiConstants : Constants.Aspetti.values()) {
                if(aspettiPresenti.contains(aspettiConstants.getCode())) {
                    descrizioneTemaNatale += "<br>" + aspettiConstants.getName()+": "+aspettiPianetiProperties.getProperty( String.valueOf(aspettiConstants.getCode())+"_min");
                }
            }
            descrizioneTemaNatale += "</p>";
        }

        return descrizioneTemaNatale;
    }






}

