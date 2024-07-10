package com.lunasapiens.zodiac;

import com.lunasapiens.AppConfig;
import com.lunasapiens.Constants;
import com.lunasapiens.dto.GiornoOraPosizioneDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Component
public class ServizioTemaNatale {

    private static final Logger logger = LoggerFactory.getLogger(ServizioTemaNatale.class);

    @Autowired
    private AppConfig appConfig;



    //private Double temperature = 0.5; private Integer maxTokens = 2500;


    public String temaNataleDescrizione(GiornoOraPosizioneDTO giornoOraPosizioneDTO) {

        //String descrizioneOggi = "<b>Datra di nascita:</b> " + giornoOraPosizioneDTO.getGiorno() + "/" + giornoOraPosizioneDTO.getMese() + "/" + giornoOraPosizioneDTO.getAnno()
        //        + " ore " + String.format("%02d", giornoOraPosizioneDTO.getOra()) + ":" + String.format("%02d", giornoOraPosizioneDTO.getMinuti()) + "\n\n" +

        Properties caseSignificato = appConfig.caseSignificato();
        Properties aspettiPianetiProperties = appConfig.AspettiPianeti();
        BuildInfoAstrologiaSwiss buildInfoAstroSwiss = new BuildInfoAstrologiaSwiss();

        String descrizioneTemaNatale = "<p>" + "<h3>Case:</h3>";
        for (CasePlacide var : buildInfoAstroSwiss.getCasePlacide(giornoOraPosizioneDTO)) {

            descrizioneTemaNatale += "<br><b>" + var.descrizioneCasaGradi() + "</b>";

            descrizioneTemaNatale += "<br>" + caseSignificato.getProperty(var.getNomeCasa());

            System.out.println(var.toString());
        }
        descrizioneTemaNatale += "</p>";


        descrizioneTemaNatale += "<p>" + "<h3>Transiti:</h3>";
        ArrayList<PianetaPosizTransito> pianetiTransiti = buildInfoAstroSwiss.getPianetiTransiti(giornoOraPosizioneDTO, appConfig.transitiPianetiSegni_TemaNatale());
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
                descrizioneTemaNatale += "<br>" + var.descrizione_Pianeta_Gradi_Retrogrado_SignificatoPianetaSegno();
            }
        }
        descrizioneTemaNatale += "</p>";


        // ASPETTI
        ArrayList<Aspetti> aspetti = CalcoloAspetti.verificaAspetti(pianetiTransiti, appConfig.AspettiPianeti());
        List<Integer> aspettiPresenti = new ArrayList<>();
        if (!aspetti.isEmpty()) {
            descrizioneTemaNatale += "<p>" + "<h3>Aspetti:</h3>";
            for (Aspetti var : aspetti) {
                descrizioneTemaNatale += "<br>" + var.getNomePianeta_1() + " e " + var.getNomePianeta_2() + " sono in " + Constants.Aspetti.fromCode(var.getTipoAspetto()).getName();
                aspettiPresenti.add(var.getTipoAspetto());
            }
        }
        descrizioneTemaNatale += "</p>";


        if(aspetti != null && !aspetti.isEmpty()){
            descrizioneTemaNatale += "<p>" + "<h3>Significato Aspetti:</h3>";
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

