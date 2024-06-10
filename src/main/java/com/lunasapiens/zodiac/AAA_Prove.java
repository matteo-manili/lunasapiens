package com.lunasapiens.zodiac;


import com.lunasapiens.Util;
import com.lunasapiens.dto.GiornoOraPosizioneDTO;
import de.thmac.swisseph.SweConst;
import de.thmac.swisseph.SweDate;
import de.thmac.swisseph.SwissEph;

import java.util.ArrayList;

public class AAA_Prove {

    public static void main(String[] args) {


        //GiornoOraPosizioneDTO giornoOraPosizioneDTO = Util.GiornoOraPosizione_Custom();


        posizionePianetiSwiss();
    }


    public static void posizionePianetiSwiss() {

        /*
        int anno = 1995;
        int mese = 6;
        int giorno = 6;
        int ora = 12;
        int minuti = 0;
        double lat = 41.89;
        double lon = 12.48;
         */

        int giorno = 9;
        int mese = 6;
        int anno = 2024;

        int ora = 0;
        int minuti = 39;
        double lat = 41.89;
        double lon = 12.48;


        //---------------------------




        //-----------------------------

        GiornoOraPosizioneDTO giornoOraPosizioneDTO = new GiornoOraPosizioneDTO(ora, minuti, giorno, mese, anno, lat, lon);

        BuildInfoAstrologiaSwiss buildInfoAstroSwiss = new BuildInfoAstrologiaSwiss();

        ArrayList<PianetaPosizione> pianetiTransiti = buildInfoAstroSwiss.getPianetiTransiti(giornoOraPosizioneDTO);

        PianetiAspetti pianetiAspetti = new PianetiAspetti( pianetiTransiti );

        ArrayList<CasePlacide> aa = buildInfoAstroSwiss.getCasePlacide(giornoOraPosizioneDTO);




    }

}