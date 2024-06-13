package com.lunasapiens.zodiac;

import com.lunasapiens.AppConfig;
import com.lunasapiens.Constants;
import com.lunasapiens.Util;
import com.lunasapiens.dto.GiornoOraPosizioneDTO;
import de.thmac.swisseph.SweConst;
import de.thmac.swisseph.SwissEph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

@Component
public class BuildInfoAstrologiaSwiss {

    private static final Logger logger = LoggerFactory.getLogger(BuildInfoAstrologiaSwiss.class);


    @Autowired
    private AppConfig appConfig;


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
     *
     * @param giornOraPosDTO
     * @return
     */
    public ArrayList<PianetaPosizione> getPianetiTransiti(GiornoOraPosizioneDTO giornOraPosDTO) {

        SwissEph swissEph = new SwissEph();
        //System.out.println("Versione SwissEph: " + swissEph.swe_java_version());
        ArrayList<PianetaPosizione> pianetaPosizioneArrayList = new ArrayList<PianetaPosizione>();

        double[] position = new double[6]; // Ecli

        // ptic position (x, y, z) and speed (dx, dy, dz)
        double julianDate = Util.convertiGiornoOraPosizioneDTO_in_JulianDate(giornOraPosDTO);

        logger.info("julianDate: " + julianDate);

        int[] planetIds = {
                SweConst.SE_SUN, SweConst.SE_MOON, SweConst.SE_MERCURY, SweConst.SE_VENUS,
                SweConst.SE_MARS, SweConst.SE_JUPITER, SweConst.SE_SATURN,
                SweConst.SE_URANUS, SweConst.SE_NEPTUNE, SweConst.SE_PLUTO };


        Properties transitiPianetiSegniProperties = appConfig.transitiPianetiSegni();

        for (int i = 0; i < planetIds.length; i++) {
            //iflag - Un flag che contiene specifiche dettagliate su come deve essere calcolato il corpo. Vedere SweConst per un elenco di flag validi (SEFLG_*).

            int iflag = SweConst.SEFLG_SWIEPH | SweConst.SEFLG_SPEED; // è possibile inserirli più di uno comeparametro del meotdo swissEph.swe_calc_ut

            // SweConst.SEFLG_SPEED3 // Calcola velocità dei pianeti utilizzando un metodo di terzo ordine
            // SweConst.SEFLG_SPEED // Calcola la velocità dei corpi celesti (posizioni e velocità)
            // SweConst.SEFLG_TRANSIT_SPEED // Calcola transiti basati sulla velocità

            // SweConst.SEFLG_SWIEPH // Utilizza le effemeridi Swiss Ephemeris per i calcoli (solitamente il default per gli usi astrologici)
            int result = swissEph.swe_calc_ut(julianDate, planetIds[i], /* SweConst.SEFLG_SWIEPH */ iflag, position, new StringBuffer());


            // Check if calculation was successful
            if (result != SweConst.ERR) {
                // Print the calculated position
                //System.out.println("Ecliptic Longitude: " + position[0]);
                //System.out.println("Ecliptic Latitude: " + position[1]);
                //System.out.println("Distance from Earth (AU): " + position[2]);
                //System.out.println("Speed Longitude: " + position[3]);
                //System.out.println("Speed Latitude: " + position[4]);
                //System.out.println("Speed Distance: " + position[5]);

                boolean retrogrado = position[3] < 0 ? true : false;

                Map.Entry<Integer, String> entry = Util.determinaSegnoZodiacale(position[0]).entrySet().iterator().next();
                String significatoTransitoPianetaSegno = Util.significatoTransitoPianetaSegno(transitiPianetiSegniProperties, i, entry.getKey());

                PianetaPosizione pianetaPosizione = new PianetaPosizione(i, Constants.Pianeti[i], position[0], 0, 0,
                        entry.getKey(), entry.getValue(), retrogrado, significatoTransitoPianetaSegno);

                pianetaPosizioneArrayList.add(pianetaPosizione);

                logger.info("Result: "+result +" "+ Constants.Pianeti[i] + ": " + position[0] + "° " + Util.determinaSegnoZodiacale(position[0]) + " retrogrado: "+retrogrado );
            } else {
                // Print error message if calculation failed
                System.err.println("Calculation failed with error code: " + result);
            }
        }
        swissEph.swe_close();
        return pianetaPosizioneArrayList;
    }




    /**
     * vedere http://th-mack.de/download/swisseph-doc/
     *
     * Nei vari siti web sembra che viene usato quello Siderale
     *
     * Se aggiungi la costante SweConst.SEFLG_SIDEREAL al metodo swe_houses, il calcolo delle posizioni delle case astrologiche verrà effettuato
     * nel sistema siderale anziché nel sistema tropicale. Questo significa che le posizioni delle case saranno calcolate in base alle costellazioni
     * fisse anziché alla posizione apparente del Sole. Altrimenti inserire valore 0 oppure SweConst.SEFLG_SIDEREAL and/or SEFLG_RADIANS
     * @return
     */
    public ArrayList<CasePlacide> getCasePlacide(GiornoOraPosizioneDTO giornOraPosDTO) {

        SwissEph swissEph = new SwissEph();

        ArrayList<CasePlacide> casePlacides = new ArrayList<CasePlacide>();

        // Calcolo delle case astrologiche
        double[] cusps = new double[13]; // Array per memorizzare le posizioni delle case
        double[] ascmc = new double[10]; // Array per memorizzare altri punti vitali


        // Se aggiungi la costante SweConst.SEFLG_SIDEREAL al metodo swe_houses (secondo parametro), il calcolo delle posizioni delle case astrologiche
        // verrà effettuato nel sistema siderale anziché nel sistema tropicale. Il sistema siderale è quello usato nei siti web più famosi.
        // Per il sistema tropicale usare invece SweConst.SEFLG_SWIEPH



        double julianDate = Util.convertiGiornoOraPosizioneDTO_in_JulianDate(giornOraPosDTO);
        System.out.println("julianDate case placide: " + julianDate);

        // SweConst.SEFLG_SIDEREAL con SweConst.SEFLG_SWIEPH
        int result = swissEph.swe_houses(julianDate, SweConst.SEFLG_SIDEREAL, giornOraPosDTO.getLat(), giornOraPosDTO.getLon(), 'P', cusps, ascmc);

        if (result == SweConst.OK) {
            // Stampare le posizioni delle case
            for (int i = 1; i <= 12; i++) {
                Map.Entry<Integer, String> entry = Util.determinaSegnoZodiacale(cusps[i]).entrySet().iterator().next();
                CasePlacide aa = new CasePlacide(String.valueOf(i), cusps[i], 0, 0, entry.getKey(), entry.getValue());
                casePlacides.add(aa);
                logger.info("Casa " + i + ": " + cusps[i] +" "+ entry.getValue());
            }
        } else {
            logger.info("Errore durante il calcolo delle case astrologiche: " + swissEph.swe_get_planet_name(result));
        }

        swissEph.swe_close();
        return casePlacides;
    }




}
