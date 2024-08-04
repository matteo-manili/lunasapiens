package com.lunasapiens.zodiac;

import com.lunasapiens.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Identifica gli Aspetti:
 * Confronta la distanza angolare con i gradi specifici degli aspetti principali, tenendo conto di un orbita (un margine di tolleranza) che può variare a seconda della pratica astrologica, ma generalmente è di 6-8 gradi per gli aspetti principali. Gli aspetti principali sono:
 *
 * Congiunzione: 0° (quando i pianeti sono nello stesso punto) con orbita di 10°
 * Sestile: 60° con orbita di 5°
 * Quadrato: 90° con orbita di 7°
 * Trigono: 120° con orbita di 8°
 * Opposizione: 180° con orbita di 9°
 *
 * --------------------------------------------------------------------------------
 *
 Che significa quando due pianeti sono in congiunzione?
 ChatGPT
 Quando due pianeti sono in congiunzione, significa che appaiono molto vicini l'uno all'altro nel cielo visto dalla Terra. In termini astronomici, una congiunzione si verifica quando due pianeti hanno la stessa longitudine celeste o ascensione retta. Questo può creare un effetto visivo impressionante, in cui i pianeti sembrano quasi toccarsi, anche se in realtà sono separati da grandi distanze nello spazio. Le congiunzioni possono coinvolgere pianeti, la Luna e il Sole e sono eventi piuttosto comuni e osservabili a occhio nudo o con piccoli telescopi.

 In astrologia che significa
 ChatGPT
 In astrologia, una congiunzione si verifica quando due pianeti (o un pianeta e un altro punto importante, come il Sole o la Luna) si trovano vicini nella stessa posizione dello zodiaco, spesso entro un orbita di pochi gradi l'uno dall'altro. Questa configurazione è considerata una delle più potenti e significative tra gli aspetti planetari. Le congiunzioni possono influenzare l'energia e il carattere delle persone e degli eventi in modi diversi, a seconda dei pianeti coinvolti.

 Le caratteristiche principali di una congiunzione in astrologia includono:

 Fusione di Energie: Le energie dei due corpi celesti si combinano e si intensificano. Questo può creare un'influenza forte e diretta nelle aree della vita governate dai pianeti coinvolti.

 Nuovi Inizi: Le congiunzioni sono spesso associate a nuovi inizi o punti di partenza, poiché rappresentano un momento in cui le energie dei pianeti si uniscono per la prima volta nel ciclo.

 Influenze Positive o Negative: L'effetto della congiunzione può essere benefico o malefico, a seconda della natura dei pianeti coinvolti. Ad esempio, una congiunzione tra Giove e Venere è generalmente considerata fortunata e favorevole, mentre una congiunzione tra Marte e Saturno può essere vista come più difficile e conflittuale.

 Intensificazione dei Temi: Le tematiche rappresentate dai pianeti coinvolti nella congiunzione saranno particolarmente accentuate e presenti nella vita della persona o nel contesto di un evento.

 In sintesi, in astrologia, una congiunzione rappresenta un momento di grande importanza in cui le energie di due pianeti si uniscono, creando un'influenza potente che può portare cambiamenti significativi.

 E quando si verificano i trigoni e quadrati ecc?
 ChatGPT
 In astrologia, oltre alla congiunzione, ci sono altri aspetti significativi tra i pianeti, ciascuno con la propria interpretazione e influenza. Ecco una panoramica dei principali aspetti:

 Trigono (Trine):

 Definizione: Si verifica quando due pianeti sono separati da un angolo di 120 gradi.
 Interpretazione: È considerato un aspetto armonico e favorevole. Indica flusso facile di energie tra i pianeti coinvolti, portando fortuna, crescita e facilità nelle aree della vita governate dai pianeti in trigono.
 Quadrato (Square):

 Definizione: Si verifica quando due pianeti sono separati da un angolo di 90 gradi.
 Interpretazione: È considerato un aspetto dinamico e sfidante. Indica tensioni, conflitti e ostacoli che richiedono sforzo e risoluzione. Tuttavia, queste sfide possono portare a crescita e sviluppo personale se affrontate con successo.
 Opposizione:

 Definizione: Si verifica quando due pianeti sono separati da un angolo di 180 gradi.
 Interpretazione: È considerato un aspetto di polarità e tensione. Indica la necessità di equilibrio e confronto tra le energie dei pianeti coinvolti. Può portare a conflitti ma anche a opportunità di comprensione e integrazione.
 Sestile (Sextile):

 Definizione: Si verifica quando due pianeti sono separati da un angolo di 60 gradi.
 Interpretazione: È considerato un aspetto armonico e favorevole, simile al trigono ma meno potente. Indica opportunità e supporto tra i pianeti coinvolti, facilitando la cooperazione e il progresso.
 Quincunx (o Incongiunzione):

 Definizione: Si verifica quando due pianeti sono separati da un angolo di 150 gradi.
 Interpretazione: È considerato un aspetto di adattamento e aggiustamento. Indica aree di vita dove c'è bisogno di aggiustamenti e compromessi per trovare un equilibrio.
 Ogni aspetto ha un'influenza specifica e unica sul tema natale di una persona o su un evento, contribuendo alla complessità e alla profondità dell'interpretazione astrologica.

 Se un pianeta è retrogrado che influssi ha?
 ChatGPT
 Quando un pianeta è retrogrado in astrologia, il suo movimento apparente visto dalla Terra sembra essere contrario al normale moto diretto. Questo fenomeno può avere diverse interpretazioni e influenze:

 Riflessione interna: Il periodo di retrogradazione di un pianeta spesso porta a un periodo di riflessione interiore. Può esserci un'opportunità per riconsiderare, revisionare e rivalutare le questioni associate al pianeta retrogrado nelle aree della vita che esso governa nel tema natale.

 Ritardo o frustrazione: A livello pratico, un pianeta retrogrado può portare a ritardi, ostacoli o frustrazioni nelle attività associate a quel pianeta. Ad esempio, Mercurio retrogrado può causare malintesi nelle comunicazioni, mentre Marte retrogrado può portare a rallentamenti nelle azioni e nei progetti.

 Ripetizione di temi passati: Le situazioni che emergono durante un periodo di retrogradazione di un pianeta possono spesso essere connesse a questioni non risolte o esperienze del passato che richiedono attenzione o completamento.

 Introspezione e crescita: Nonostante le sfide, un pianeta retrogrado può offrire un'opportunità preziosa per un'autoriflessione più profonda e per affrontare eventuali problemi che non sono stati adeguatamente trattati in passato. Può essere un momento di crescita personale e di consapevolezza.

 Ritorno al passato: A livello karmico, la retrogradazione di un pianeta può indicare un periodo in cui si riprendono relazioni o situazioni passate per risolvere e completare le questioni in sospeso.

 È importante considerare anche il segno zodiacale e la casa astrologica in cui avviene la retrogradazione del pianeta per una comprensione più dettagliata dei suoi effetti nel tema natale di una persona.
 */

public class CalcoloAspetti {

    private static final Logger logger = LoggerFactory.getLogger(CalcoloAspetti.class);

    // Definisci i valori angolari degli aspetti principali
    private static final int CONGIUNZIONE = 0;
    private static final int SESTILE = 60;
    private static final int QUADRATO = 90;
    private static final int TRIGONO = 120;
    private static final int OPPOSIZIONE = 180;

    // Definisci i valori angolari degli aspetti minori
    private static final int SEMISESTILE = 30;
    private static final int SEMIQUADRATO = 45;
    private static final int SESQUIQUADRATO = 135;
    private static final int QUINCONCE = 150;

    // Orbite specifiche per ogni aspetto
    private static final int ORB_CONGIUNZIONE = 10;
    private static final int ORB_SESTILE = 5;
    private static final int ORB_QUADRATO = 7;
    private static final int ORB_TRIGONO = 8;
    private static final int ORB_OPPOSIZIONE = 9;
    private static final int ORB_MINORE = 2; // Orbita per gli aspetti minori



    public static ArrayList<Aspetti> verificaAspetti(List<PianetaPosizTransito> pianeti, Properties aspettiPianeti) {

        ArrayList<Aspetti> aspettiArrayList = new ArrayList<>();

        for (int i = 0; i < pianeti.size(); i++) {
            for (int j = i + 1; j < pianeti.size(); j++) {
                PianetaPosizTransito p1 = pianeti.get(i);
                PianetaPosizTransito p2 = pianeti.get(j);

                double angolo1 = p1.getGradi();
                double angolo2 = p2.getGradi();

                double differenzaAngolare = Math.abs(angolo1 - angolo2);
                if (differenzaAngolare > 180) {
                    differenzaAngolare = 360 - differenzaAngolare;
                }

                if (isAspetto(differenzaAngolare, CONGIUNZIONE, ORB_CONGIUNZIONE)) {
                    aspettiArrayList.add(new Aspetti(p1.getNumeroPianeta(), p1.getNomePianeta(), p2.getNumeroPianeta(), p2.getNomePianeta(),
                            Constants.Aspetti.fromCode(Constants.Aspetti.CONGIUNZIONE.getCode()).getCode(), aspettiPianeti.getProperty( String.valueOf(Constants.Aspetti.CONGIUNZIONE.getCode()) )));

                } else if (isAspetto(differenzaAngolare, SESTILE, ORB_SESTILE)) {
                    aspettiArrayList.add(new Aspetti(p1.getNumeroPianeta(), p1.getNomePianeta(), p2.getNumeroPianeta(), p2.getNomePianeta(),
                            Constants.Aspetti.fromCode(Constants.Aspetti.SESTILE.getCode()).getCode(), aspettiPianeti.getProperty( String.valueOf(Constants.Aspetti.SESTILE.getCode()) )));

                } else if (isAspetto(differenzaAngolare, QUADRATO, ORB_QUADRATO)) {
                    aspettiArrayList.add(new Aspetti(p1.getNumeroPianeta(), p1.getNomePianeta(), p2.getNumeroPianeta(), p2.getNomePianeta(),
                            Constants.Aspetti.fromCode(Constants.Aspetti.QUADRATO.getCode()).getCode(), aspettiPianeti.getProperty( String.valueOf(Constants.Aspetti.QUADRATO.getCode()) )));

                } else if (isAspetto(differenzaAngolare, TRIGONO, ORB_TRIGONO)) {
                    aspettiArrayList.add(new Aspetti(p1.getNumeroPianeta(), p1.getNomePianeta(), p2.getNumeroPianeta(), p2.getNomePianeta(),
                            Constants.Aspetti.fromCode(Constants.Aspetti.TRIGONO.getCode()).getCode(), aspettiPianeti.getProperty( String.valueOf(Constants.Aspetti.TRIGONO.getCode()) )));

                } else if (isAspetto(differenzaAngolare, OPPOSIZIONE, ORB_OPPOSIZIONE)) {
                    aspettiArrayList.add(new Aspetti(p1.getNumeroPianeta(), p1.getNomePianeta(), p2.getNumeroPianeta(), p2.getNomePianeta(),
                            Constants.Aspetti.fromCode(Constants.Aspetti.OPPOSIZIONE.getCode()).getCode(), aspettiPianeti.getProperty( String.valueOf(Constants.Aspetti.OPPOSIZIONE.getCode()) )));
                }
                /* else if (isAspetto(differenzaAngolare, SEMISESTILE, ORB_MINORE)) {
                    aspetti.add(p1.getNomePianeta() + " e " + p2.getNomePianeta() + " sono in Semisestile");
                } else if (isAspetto(differenzaAngolare, SEMIQUADRATO, ORB_MINORE)) {
                    aspetti.add(p1.getNomePianeta() + " e " + p2.getNomePianeta() + " sono in Semiquadrato");
                } else if (isAspetto(differenzaAngolare, SESQUIQUADRATO, ORB_MINORE)) {
                    aspetti.add(p1.getNomePianeta() + " e " + p2.getNomePianeta() + " sono in Sesquiquadrato"); */
                else if (isAspetto(differenzaAngolare, QUINCONCE, ORB_MINORE)) {
                    aspettiArrayList.add(new Aspetti(p1.getNumeroPianeta(), p1.getNomePianeta(), p2.getNumeroPianeta(), p2.getNomePianeta(),
                            Constants.Aspetti.fromCode(Constants.Aspetti.QUINCUNX.getCode()).getCode(), aspettiPianeti.getProperty( String.valueOf(Constants.Aspetti.QUINCUNX.getCode()) )));
                }

            }
        }
        return aspettiArrayList;
    }

    private static boolean isAspetto(double differenzaAngolare, int aspetto, int orb) {
        return Math.abs(differenzaAngolare - aspetto) <= orb;
    }

}
