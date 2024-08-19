package com.lunasapiens.zodiac;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AAA_Prove {

    private static final Logger logger = LoggerFactory.getLogger(AAA_Prove.class);

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
        /*
        GiornoOraPosizioneDTO giornoOraPosizioneDTO = new GiornoOraPosizioneDTO(ora, minuti, giorno, mese, anno, lat, lon);
        BuildInfoAstrologiaSwiss buildInfoAstroSwiss = new BuildInfoAstrologiaSwiss();
        ArrayList<PianetaPosizione> pianetiTransiti = buildInfoAstroSwiss.getPianetiTransiti(giornoOraPosizioneDTO);
        PianetiAspetti pianetiAspetti = new PianetiAspetti( pianetiTransiti );
        ArrayList<CasePlacide> aa = buildInfoAstroSwiss.getCasePlacide(giornoOraPosizioneDTO);
        */






    }

}