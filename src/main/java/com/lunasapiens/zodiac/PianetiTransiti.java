package com.lunasapiens.zodiac;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * Identifica gli Aspetti:
 * Confronta la distanza angolare con i gradi specifici degli aspetti principali, tenendo conto di un orbita (un margine di tolleranza) che può variare a seconda della pratica astrologica, ma generalmente è di 6-8 gradi per gli aspetti principali. Gli aspetti principali sono:
 *
 * Congiunzione: 0° (quando i pianeti sono nello stesso punto)
 * Sestile: 60°
 * Quadrato: 90°
 * Trigono: 120°
 * Opposizione: 180°
 *
 * --------------------------------------------------------------------------------
 *
 * Aspetti Minori
 * Semisestile (30°)
 *
 * Descrizione: I pianeti sono a 30 gradi di distanza.
 * Significato: Aspetto minore che indica piccole opportunità e lievi facilitazioni. Può suggerire lievi abilità che, con sforzo, possono essere sviluppate.
 * Semiquadrato (45°)
 *
 * Descrizione: I pianeti sono a 45 gradi di distanza.
 * Significato: Aspetto minore di tensione che può indicare fastidi o sfide minori. Richiede attenzione per evitare irritazioni continue.
 * Sesquiquadrato (135°)
 *
 * Descrizione: I pianeti sono a 135 gradi di distanza.
 * Significato: Aspetto di tensione che indica sfide che possono sembrare fuori proporzione ma che necessitano di lavoro per essere risolte.
 * Quinconce (150°)
 *
 * Descrizione: I pianeti sono a 150 gradi di distanza.
 * Significato: Aspetto di inadeguatezza o necessità di adattamento. Indica situazioni che richiedono aggiustamenti e riorganizzazioni.
 * Interpretazione degli Aspetti
 * Aspetti Armonici (Sestile, Trigono): Facilitano il flusso di energie e indicano aree di talento naturale e facile sviluppo.
 * Aspetti Dinamici (Congiunzione, Quadrato, Opposizione): Creano sfide e opportunità di crescita. Richiedono sforzo per integrare le energie coinvolte.
 */


public class PianetiTransiti {

    private static final Logger logger = LoggerFactory.getLogger(PianetiTransiti.class);

    // Definisci i valori angolari degli aspetti principali
    private static final int CONGIUNZIONE = 0;
    private static final int SESTILE = 60;
    private static final int QUADRATO = 90;
    private static final int TRIGONO = 120;
    private static final int OPPOSIZIONE = 180;
    private static final int ORB = 2; // Orbita di tolleranza in gradi

    public PianetiTransiti() { }

    public PianetiTransiti(ArrayList<PianetaPosizione> pianeti) {
        ArrayList<String> aspetti = verificaAspetti(pianeti);
        for (String aspetto : aspetti) {
            logger.info(aspetto);
        }
    }



    public ArrayList<String> verificaAspetti(ArrayList<PianetaPosizione> pianeti) {
        ArrayList<String> aspetti = new ArrayList<>();

        for (int i = 0; i < pianeti.size(); i++) {
            for (int j = i + 1; j < pianeti.size(); j++) {
                PianetaPosizione p1 = pianeti.get(i);
                PianetaPosizione p2 = pianeti.get(j);

                double angolo1 = p1.getGradi();
                double angolo2 = p2.getGradi();

                double differenzaAngolare = Math.abs(angolo1 - angolo2);
                if (differenzaAngolare > 180) {
                    differenzaAngolare = 360 - differenzaAngolare;
                }

                if (isAspetto(differenzaAngolare, CONGIUNZIONE)) {
                    aspetti.add(p1.getNomePianeta() + " e " + p2.getNomePianeta() + " sono in Congiunzione");
                } else if (isAspetto(differenzaAngolare, SESTILE)) {
                    aspetti.add(p1.getNomePianeta() + " e " + p2.getNomePianeta() + " sono in Sestile");
                } else if (isAspetto(differenzaAngolare, QUADRATO)) {
                    aspetti.add(p1.getNomePianeta() + " e " + p2.getNomePianeta() + " sono in Quadrato");
                } else if (isAspetto(differenzaAngolare, TRIGONO)) {
                    aspetti.add(p1.getNomePianeta() + " e " + p2.getNomePianeta() + " sono in Trigono");
                } else if (isAspetto(differenzaAngolare, OPPOSIZIONE)) {
                    aspetti.add(p1.getNomePianeta() + " e " + p2.getNomePianeta() + " sono in Opposizione");
                }
            }
        }

        return aspetti;
    }

    private static boolean isAspetto(double differenzaAngolare, int aspetto) {
        return Math.abs(differenzaAngolare - aspetto) <= ORB;
    }


}
