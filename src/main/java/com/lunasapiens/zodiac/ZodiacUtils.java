package com.lunasapiens.zodiac;

import com.lunasapiens.Constants;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ZodiacUtils {

    private static final Logger logger = LoggerFactory.getLogger(ZodiacUtils.class);




    // ###################################### UTILS ######################################



    protected static double convertDegreesToDecimal(String position) {
        Pair<Integer, Integer> pair = dammiGradiEMinutiPair(position);
        return pair.getKey() + (pair.getValue() / 60.0);
    }

    protected static Pair dammiGradiEMinutiPair(String position) {
        String[] parts = position.split("°|'");
        int degrees = Integer.parseInt(parts[0].trim());
        int minutes = Integer.parseInt(parts[1].replace("’", "").trim());
        Pair<Integer, Integer> pair = new Pair<>(degrees, minutes);
        return pair;
    }


    public static double calcolaPosizionePianetaOpposto(double posizioneNodo) {
        if (posizioneNodo < 0 || posizioneNodo >= 360) {
            throw new IllegalArgumentException("La posizione del nodo deve essere compresa tra 0 e 360 gradi.");
        }
        return (posizioneNodo + 180) % 360;
    }

    public static Pair<Integer, Integer> calcolaPosizionePianetaOppostoInGradiEMinuti(int gradi, int minuti) {
        // Calcola la posizione in gradi decimali
        double posizioneDecimale = gradi + (minuti / 60.0);

        // Calcola la posizione opposta in gradi decimali
        double posizioneOppostaDecimale = (posizioneDecimale + 180) % 360;

        // Converte la posizione opposta in gradi e minuti
        int gradiOpposti = (int) posizioneOppostaDecimale;
        int minutiOpposti = (int) ((posizioneOppostaDecimale - gradiOpposti) * 60);

        // Restituisce la posizione opposta come un oggetto Pair (gradi, minuti)
        return new Pair<>(gradiOpposti, minutiOpposti);
    }

    public static Constants.SegniZodiacali calcolaSegnoOpposto(Constants.SegniZodiacali segnoZodiacale) {
        // Calcola il numero del segno opposto
        int numeroOpposto = (segnoZodiacale.getNumero() + 6) % 12;
        // Usa il metodo `fromNumero` per ottenere il segno opposto
        return Constants.SegniZodiacali.fromNumero(numeroOpposto);
    }

    /**
     * Da decimalDegrees a Gradi e Minuti
     */
    protected static String convertToDegreesAndMinutes(double decimalDegrees) {
        // Estrai la parte intera dei gradi
        int degrees = (int) decimalDegrees;

        // Calcola la parte frazionaria e convertila in minuti
        double minutesDecimal = (decimalDegrees - degrees) * 60;
        int minutes = (int) Math.round(minutesDecimal);

        // Gestisci il caso in cui i minuti arrotondati potrebbero essere 60
        if (minutes == 60) {
            degrees++;
            minutes = 0;
        }

        // Restituisci il risultato come una stringa in formato "gradi°minuti'"
        return String.format("%d° %d'", degrees, minutes);
    }


    protected static Pair convertCoordinataToDegreesMinutes(double coordinata) {
        int degrees = (int) coordinata;
        double fractionalPart = coordinata - degrees;
        // Convertire la parte decimale in minuti
        double minutes = fractionalPart * 60;
        int minutesInt = (int) minutes;
        double seconds = (minutes - minutesInt) * 60;
        Pair<Integer, Integer> pair = new Pair<>(degrees, minutesInt);
        return pair;
    }




    protected static void assegnaCaseAiPianeti(List<Pianeti> pianetiTransiti, List<CasePlacide> casePlacideArrayList) {
        // Creare una copia della lista delle case per ordinarla
        List<CasePlacide> caseOrdinate = new ArrayList<>(casePlacideArrayList);
        caseOrdinate.sort(Comparator.comparingDouble(CasePlacide::getGradi));

        for (Pianeti pianeta : pianetiTransiti) {
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




    /**
     * Metodo per determinare il segno zodiacale in base al grado
     * @param grado
     * @return
     */
    protected static Map<Integer, String> determinaSegnoZodiacale(double grado) {
        Map<Integer, String> segniZodiacali = new HashMap<>();
        List<String> nomiSegni = Constants.SegniZodiacali.getAllNomi();
        if (grado >= Constants.SegniZodiacali.ARIETE.getGradi() && grado < 30.0d) {                 // Ariete
            segniZodiacali.put(0, nomiSegni.get(0));
        } else if (grado >= Constants.SegniZodiacali.TORO.getGradi() && grado < 60.0d) {         // Toro
            segniZodiacali.put(1, nomiSegni.get(1));
        } else if (grado >= Constants.SegniZodiacali.GEMELLI.getGradi() && grado < 90.0d) {         // Gemelli
            segniZodiacali.put(2, nomiSegni.get(2));
        } else if (grado >= Constants.SegniZodiacali.CANCRO.getGradi() && grado < 120.0d) {        // Cancro
            segniZodiacali.put(3, nomiSegni.get(3));
        } else if (grado >= Constants.SegniZodiacali.LEONE.getGradi() && grado < 150.0d) {       // Leone
            segniZodiacali.put(4, nomiSegni.get(4));
        } else if (grado >= Constants.SegniZodiacali.VERGINE.getGradi() && grado < 180.0d) {       // Vergine
            segniZodiacali.put(5, nomiSegni.get(5));
        } else if (grado >= Constants.SegniZodiacali.BILANCIA.getGradi() && grado < 210.0d) {       // Bilancia
            segniZodiacali.put(6, nomiSegni.get(6));
        } else if (grado >= Constants.SegniZodiacali.SCORPIONE.getGradi() && grado < 240.0d) {       // Scorpione
            segniZodiacali.put(7, nomiSegni.get(7));
        } else if (grado >= Constants.SegniZodiacali.SAGITTARIO.getGradi() && grado < 270.0d) {       // Sagittario
            segniZodiacali.put(8, nomiSegni.get(8));
        } else if (grado >= Constants.SegniZodiacali.CAPRICORNO.getGradi() && grado < 300.0d) {       // Capricorno
            segniZodiacali.put(9, nomiSegni.get(9));
        } else if (grado >= Constants.SegniZodiacali.ACQUARIO.getGradi() && grado < 330.0d) {       // Acquario
            segniZodiacali.put(10, nomiSegni.get(10));
        } else if (grado >= Constants.SegniZodiacali.PESCI.getGradi() && grado < 360.0d) {       // Pesci
            segniZodiacali.put(11, nomiSegni.get(11));
        } else {
            segniZodiacali.put(-1, "Grado non valido");
        }
        return segniZodiacali;
    }




    protected static String significatoTransitoPianetaSegno(Properties properties, int numero1, int numero2) {
        // Costruisci la chiave per recuperare il valore desiderato
        String chiaveProperties = numero1 + "_" + numero2;
        // Recupera il significato del pianeta
        String significato = properties.getProperty(chiaveProperties);
        return significato;
    }



}
