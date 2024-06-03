package com.lunasapiens.zodiac;


import com.lunasapiens.Util;
import com.lunasapiens.dto.GiornoOraPosizioneDTO;
import de.thmac.swisseph.SweConst;
import de.thmac.swisseph.SweDate;
import de.thmac.swisseph.SwissEph;

import java.util.ArrayList;

public class AAA_Prove {

    public static void main(String[] args) {


        GiornoOraPosizioneDTO giornoOraPosizioneDTO = Util.GiornoOraPosizione_Custom();



        String descrizioneOggi = "Oggi è: " + giornoOraPosizioneDTO.getGiorno() + "/" + giornoOraPosizioneDTO.getMese() + "/" + giornoOraPosizioneDTO.getAnno()
                + " ore " + giornoOraPosizioneDTO.getOra() + ":" + giornoOraPosizioneDTO.getMinuti() + "\n" +
                "Transiti di oggi: " + "\n";

        posizionePianetiSwiss();
    }


    public static void posizionePianetiSwiss() {

        int anno = 1965;
        int mese = 11;
        int giorno = 6;
        int ora = 4;
        int minuti = 20;
        double lat = 41.89;
        double lon = 12.48;

        GiornoOraPosizioneDTO giornoOraPosizioneDTO
                = new GiornoOraPosizioneDTO(ora, minuti, giorno, mese, anno, lon, lat);

        BuildInfoAstrologiaSwiss buildInfoAstroSwiss = new BuildInfoAstrologiaSwiss();

        ArrayList<PianetaPosizione> pianetiTransiti = buildInfoAstroSwiss.getPianetiTransiti(giornoOraPosizioneDTO);

        PianetiAspetti pianetiAspetti = new PianetiAspetti( pianetiTransiti );

        buildInfoAstroSwiss.getCasePlacide(giornoOraPosizioneDTO);
    }

}