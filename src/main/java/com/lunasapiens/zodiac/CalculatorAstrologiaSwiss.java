package com.lunasapiens.zodiac;

import com.lunasapiens.Constants;
import com.lunasapiens.utils.Utils;
import com.lunasapiens.dto.GiornoOraPosizioneDTO;
import com.lunasapiens.utils.UtilsZodiac;
import de.thmac.swisseph.SweConst;
import de.thmac.swisseph.SwissEph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

/**
 * vedere http://th-mack.de/download/swisseph-doc/
 *
 * // SweConst.SEFLG_JPLEPH // Utilizza le effemeridi JPL (Jet Propulsion Laboratory) per i calcoli
 * // SweConst.SEFLG_SWIEPH // Utilizza le effemeridi Swiss Ephemeris per i calcoli (solitamente il default per gli usi astrologici)
 * // SweConst.SEFLG_MOSEPH // Utilizza le effemeridi DE404 di Moshier per i calcoli
 * // SweConst.SEFLG_DEFAULTEPH // Alias per SEFLG_SWIEPH, utilizza le effemeridi Swiss Ephemeris come default
 * // SweConst.SEFLG_EPHMASK // Maschera per isolare i flag delle effemeridi (JPLEPH, SWIEPH, MOSEPH)
 *
 * // SweConst.SEFLG_HELCTR // Calcola posizioni eliocentriche (rispetto al centro del Sole)
 * // SweConst.SEFLG_TRUEPOS // Calcola posizioni vere (senza correzione per il tempo di luce)
 * // SweConst.SEFLG_J2000 // Posizioni rispetto all'epoca J2000.0 (1 gennaio 2000, 12:00 TT)
 * // SweConst.SEFLG_NONUT // Esclude correzioni per la nutazione
 * // SweConst.SEFLG_SPEED3 // Calcola velocità dei pianeti utilizzando un metodo di terzo ordine
 * // SweConst.SEFLG_SPEED // Calcola la velocità dei corpi celesti (posizioni e velocità)
 * // SweConst.SEFLG_NOGDEFL // Esclude correzioni per la deflessione della luce
 * // SweConst.SEFLG_NOABERR // Esclude correzioni per l'aberrazione della luce
 * // SweConst.SEFLG_EQUATORIAL // Calcola coordinate equatoriali (Ascensione Retta e Declinazione)
 * // SweConst.SEFLG_XYZ // Restituisce le coordinate cartesiane (x, y, z) anziché polari
 * // SweConst.SEFLG_RADIANS // Restituisce le coordinate in radianti anziché in gradi
 * // SweConst.SEFLG_BARYCTR // Calcola posizioni baricentriche (rispetto al centro di massa del sistema solare)
 * // SweConst.SEFLG_TOPOCTR // Calcola posizioni topocentriche (rispetto alla superficie della Terra)
 * // SweConst.SEFLG_SIDEREAL // Usa il sistema di coordinate siderali
 * // SweConst.SEFLG_ICRS // Usa il sistema di coordinate ICRS (International Celestial Reference System)
 * // SweConst.SEFLG_DPSIDEPS_1980 // Usa i parametri dpsi/deps secondo lo standard IAU 1980
 * // SweConst.SEFLG_JPLHOR // Usa i dati e metodi di calcolo del JPL Horizons
 * // SweConst.SEFLG_JPLHOR_APPROX // Usa un'approssimazione dei dati JPL Horizons
 * // SweConst.SEFLG_TRANSIT_LONGITUDE // Calcola transiti basati sulla longitudine
 * // SweConst.SEFLG_TRANSIT_LATITUDE // Calcola transiti basati sulla latitudine
 * // SweConst.SEFLG_TRANSIT_DISTANCE // Calcola transiti basati sulla distanza
 * // SweConst.SEFLG_TRANSIT_SPEED // Calcola transiti basati sulla velocità
 * // SweConst.SEFLG_YOGA_TRANSIT // Calcola transiti secondo le configurazioni yoga (astrologia indiana)
 * // SweConst.SEFLG_PARTILE_TRANSIT_START // Inizio del transito partile (quando il pianeta è esattamente congiunto)
 * // SweConst.SEFLG_PARTILE_TRANSIT_END // Fine del transito partile
 * // SweConst.SEFLG_PARTILE_TRANSIT // Transito partile (pianeta esattamente congiunto a un altro punto)
 */
@Service
public class CalculatorAstrologiaSwiss {

    private static final Logger logger = LoggerFactory.getLogger(CalculatorAstrologiaSwiss.class);


    public ArrayList<Pianeti> getPianetiTransiti(GiornoOraPosizioneDTO giornOraPosDTO, Properties transitiPianetiSegniProperties) {
        SwissEph swissEph = new SwissEph();
        try {
            //System.out.println("Versione SwissEph: " + swissEph.swe_java_version());
            ArrayList<Pianeti> pianetaArrayList = new ArrayList<Pianeti>();

            double[] position = new double[6]; // Ecli
            // ptic position (x, y, z) and speed (dx, dy, dz)
            double julianDate = Utils.convertiGiornoOraPosizioneDTO_in_JulianDate(giornOraPosDTO);

            //System.out.println("julianDate: " + julianDate);

            int[] planetIds = {
                    SweConst.SE_SUN,
                    SweConst.SE_MOON,
                    SweConst.SE_MERCURY,
                    SweConst.SE_VENUS,
                    SweConst.SE_MARS,
                    SweConst.SE_JUPITER,
                    SweConst.SE_SATURN,
                    SweConst.SE_URANUS,
                    SweConst.SE_NEPTUNE,
                    SweConst.SE_PLUTO,
                    SweConst.SE_MEAN_NODE, // Nodo Nord, da questo si determina il Nod Sud
                    SweConst.SE_MEAN_APOG, // Lilith Medio
            };


            Constants.Pianeti[] pianetiEnum = {
                    Constants.Pianeti.SOLE,
                    Constants.Pianeti.LUNA,
                    Constants.Pianeti.MERCURIO,
                    Constants.Pianeti.VENERE,
                    Constants.Pianeti.MARTE,
                    Constants.Pianeti.GIOVE,
                    Constants.Pianeti.SATURNO,
                    Constants.Pianeti.URANO,
                    Constants.Pianeti.NETTUNO,
                    Constants.Pianeti.PLUTONE,
                    Constants.Pianeti.NODE_M,
                    Constants.Pianeti.LILITH,
            };

            //Properties transitiPianetiSegniProperties = appConfig.transitiSegniPianeti_OroscopoDelGiorno();

            for (int i = 0; i < planetIds.length; i++) {
                Constants.Pianeti pianetaEnum = pianetiEnum[i];
                //iflag - Un flag che contiene specifiche dettagliate su come deve essere calcolato il corpo. Vedere SweConst per un elenco di flag validi (SEFLG_*).
                int iflag = SweConst.SEFLG_SWIEPH | SweConst.SEFLG_SPEED; // è possibile inserirli più di uno comeparametro del meotdo swissEph.swe_calc_ut
                // SweConst.SEFLG_SPEED3 // Calcola velocità dei pianeti utilizzando un metodo di terzo ordine
                // SweConst.SEFLG_SPEED // Calcola la velocità dei corpi celesti (posizioni e velocità)
                // SweConst.SEFLG_TRANSIT_SPEED // Calcola transiti basati sulla velocità
                // SweConst.SEFLG_SWIEPH // Utilizza le effemeridi Swiss Ephemeris per i calcoli (solitamente il default per gli usi astrologici)
                int result = swissEph.swe_calc_ut(julianDate, planetIds[i], /* SweConst.SEFLG_SWIEPH */ iflag, position, new StringBuffer());

                logger.info("CALCOLO {} result={} grado={}", pianetaEnum.getNome(), result, position[0]);

                // Check if calculation was successful
                if (result != SweConst.ERR) {
                    boolean retrogrado = position[3] < 0;
                    // posizione assoluta eclittica
                    double gradoTotale = position[0];
                    // aggiungi Nodo Sud
                    if(planetIds[i] == SweConst.SE_MEAN_NODE){
                        double nodoSud = position[0] + 180;
                        if(nodoSud >= 360){
                            nodoSud -= 360;
                        }
                        Map.Entry<Integer, String> segno = UtilsZodiac.determinaSegnoZodiacale(nodoSud)
                                        .entrySet()
                                        .iterator()
                                        .next();
                        Pianeti nodoSudPianeta = new Pianeti(
                                Constants.Pianeti.NODE_S.getNumero(),
                                Constants.Pianeti.NODE_S.getNome(),
                                nodoSud,
                                (int)(nodoSud % 30),
                                0,
                                0,
                                segno.getKey(),
                                segno.getValue(),
                                retrogrado,
                                ""
                        );
                        pianetaArrayList.add(nodoSudPianeta);
                        logger.info("Nodo Sud aggiunto: {}° {}", nodoSud, segno);
                    }

                    // segno zodiacale
                    Map.Entry<Integer, String> entry =
                            UtilsZodiac.determinaSegnoZodiacale(gradoTotale)
                                    .entrySet()
                                    .iterator()
                                    .next();

                    // grado dentro il segno (0-29)
                    double gradoNelSegno = gradoTotale % 30;
                    // conversione gradi minuti secondi
                    int gradi = (int) gradoNelSegno;
                    double restoMinuti = (gradoNelSegno - gradi) * 60;
                    int minuti = (int) restoMinuti;
                    int secondi = (int)Math.round(((restoMinuti - minuti) * 60));

                    String significatoTransitoPianetaSegno = UtilsZodiac.significatoTransitoPianetaSegno(transitiPianetiSegniProperties, i, entry.getKey());
                    Pianeti pianeta = new Pianeti(
                            pianetaEnum.getNumero(),
                            pianetaEnum.getNome(),
                            gradoTotale,
                            gradi,
                            minuti,
                            secondi,
                            entry.getKey(),
                            entry.getValue(),
                            retrogrado,
                            significatoTransitoPianetaSegno
                    );
                    pianetaArrayList.add(pianeta);

                    logger.info("Result: {} {}: {}° {} retrogrado: {}",
                            result,
                            pianetaEnum.getNome(),
                            position[0],
                            UtilsZodiac.determinaSegnoZodiacale(position[0]),
                            retrogrado
                    );


                } else {
                    // Print error message if calculation failed
                    System.err.println("Calculation failed with error code: " + result);
                }
            }
            return pianetaArrayList;

        } finally {
            swissEph.swe_close();
        }
    }





    public ArrayList<CasePlacide> getCasePlacide(GiornoOraPosizioneDTO giornOraPosDTO) {
        SwissEph swissEph = new SwissEph();
        try {
            ArrayList<CasePlacide> casePlacides = new ArrayList<>();
            double julianDate = Utils.convertiGiornoOraPosizioneDTO_in_JulianDate(giornOraPosDTO);
            logger.info("julianDate case placide: " + julianDate);
            double[] cusps = new double[13];
            double[] ascmc = new double[10];

            // Placidus
            int houseSys = 'P';

            int result = swissEph.swe_houses(
                    julianDate,
                    0, // 0 significa nessun flag siderale → quindi tropicale
                    giornOraPosDTO.getLat(),
                    giornOraPosDTO.getLon(),
                    houseSys,
                    cusps,
                    ascmc
            );

            if (result == SweConst.OK) {
                // ASC
                double ascendente = ascmc[0];
                Map.Entry<Integer,String> ascEntry = UtilsZodiac.determinaSegnoZodiacale(ascendente).entrySet().iterator().next();
                logger.info("ASC: " + ascendente + " " + ascEntry.getValue());

                // MC
                double mc = ascmc[1];
                Map.Entry<Integer,String> mcEntry = UtilsZodiac.determinaSegnoZodiacale(mc).entrySet().iterator().next();
                logger.info("MC: " + mc + " " + mcEntry.getValue());
                for(int i = 1; i <= 12; i++) {
                    double gradoTotale = cusps[i];
                    Map.Entry<Integer,String> entry = UtilsZodiac.determinaSegnoZodiacale(gradoTotale).entrySet().iterator().next();
                    double gradoNelSegno = gradoTotale % 30;
                    int gradi = (int) gradoNelSegno;
                    int minuti = (int)((gradoNelSegno - gradi) * 60);
                    Constants.Case casa = Constants.Case.fromNumero(i);
                    CasePlacide cp = new CasePlacide(i, casa.getName(), gradoNelSegno, gradi, minuti, entry.getKey(), entry.getValue());
                    casePlacides.add(cp);
                    logger.info( "Casa " + i + ": " + gradi + "°" + minuti + "' " + entry.getValue());
                }

            } else {
                logger.error("Errore calcolo case SwissEph: " + result);
            }
            return casePlacides;

        } finally {
            swissEph.swe_close();
        }
    }


}
