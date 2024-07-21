package com.lunasapiens.zodiac;

import com.lunasapiens.Constants;
import com.lunasapiens.Util;
import com.lunasapiens.dto.GiornoOraPosizioneDTO;
import de.thmac.swisseph.SweConst;
import de.thmac.swisseph.SweDate;
import de.thmac.swisseph.SwissEph;
import de.thmac.swisseph.SwissLib;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;


public class BuildInfoAstrologiaSwiss {

    private static final Logger logger = LoggerFactory.getLogger(BuildInfoAstrologiaSwiss.class);
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
    public ArrayList<PianetaPosizTransito> getPianetiTransiti(GiornoOraPosizioneDTO giornOraPosDTO, Properties transitiPianetiSegniProperties) {

        SwissEph swissEph = new SwissEph();
        //System.out.println("Versione SwissEph: " + swissEph.swe_java_version());
        ArrayList<PianetaPosizTransito> pianetaPosizTransitoArrayList = new ArrayList<PianetaPosizTransito>();

        double[] position = new double[6]; // Ecli

        // ptic position (x, y, z) and speed (dx, dy, dz)
        double julianDate = Util.convertiGiornoOraPosizioneDTO_in_JulianDate(giornOraPosDTO);

        logger.info("julianDate: " + julianDate);

        int[] planetIds = {
                SweConst.SE_SUN, SweConst.SE_MOON, SweConst.SE_MERCURY, SweConst.SE_VENUS,
                SweConst.SE_MARS, SweConst.SE_JUPITER, SweConst.SE_SATURN,
                SweConst.SE_URANUS, SweConst.SE_NEPTUNE, SweConst.SE_PLUTO };


        //Properties transitiPianetiSegniProperties = appConfig.transitiSegniPianeti_OroscopoDelGiorno();

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

                PianetaPosizTransito pianetaPosizTransito = new PianetaPosizTransito(i, Constants.Pianeti.fromNumero(i).getNome(), position[0], 0, 0,
                        entry.getKey(), entry.getValue(), retrogrado, significatoTransitoPianetaSegno);

                pianetaPosizTransitoArrayList.add(pianetaPosizTransito);

                //logger.info("Result: "+result +" "+ Constants.Pianeti.fromNumero(i).getNome() + ": " + position[0] + "° " + Util.determinaSegnoZodiacale(position[0]) + " retrogrado: "+retrogrado );
            } else {
                // Print error message if calculation failed
                System.err.println("Calculation failed with error code: " + result);
            }
        }
        swissEph.swe_close();
        return pianetaPosizTransitoArrayList;
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

        // Se aggiungi la costante SweConst.SEFLG_SIDEREAL al metodo swe_houses (secondo parametro), il calcolo delle posizioni delle case astrologiche
        // verrà effettuato nel sistema siderale anziché nel sistema tropicale. Il sistema siderale è quello usato nei siti web più famosi.
        // Per il sistema tropicale usare invece SweConst.SEFLG_SWIEPH

        double julianDate = Util.convertiGiornoOraPosizioneDTO_in_JulianDate(giornOraPosDTO);
        System.out.println("julianDate case placide: " + julianDate);

        // SISTEMI DI DIVISIONE DELLE CASE: sono di tipo Mobile, fisso e altri tipi.
        // vedere la classe: class de.thmac.swisseph.SweHouse, ci sono le lettere dei sistemi di divisione di Case.
        // Le case a divisione fissa sono chiamate anche 'Sistema di case uguali' e sono la lettera: A oppure E.
        // Le case a divisione mobili sono le più usate: Placidus 'P', Koch 'K', Regiomontano/Regiomontanus 'R' e Alcabitius 'B'
        // su astro-seek.com e su simonandthestars.it usano il Placidus 'P' o il Koch 'K', ma i risultati non sono uguali.
        // i confronti con astro-seek.com danno quasi sempre l'ascendente uguale. E solo 5 volte su 10 le altre case sono uguali.
        // il flag SweConst.SEFLG_SIDEREAL è necessario, lo usa anche astro-seek.com


        // 'C':return "Campanus";

        // Calcolo delle case astrologiche
        double[] cusps = new double[13]; // Array per memorizzare le posizioni delle case
        double[] ascmc = new double[10]; // Array per memorizzare altri punti vitali
        int houseSys = (int)'P';
        // SweConst.SEFLG_SIDEREAL con SweConst.SEFLG_SWIEPH | se metto 0 i risultati sono più esatti

        //int iflag = SweConst.SEFLG_JPLEPH | SweConst.SEFLG_SIDEREAL; // Utilizza le effemeridi Swiss e il sistema siderale

        //int iflag = SweConst.SEFLG_SWIEPH; // Usa Swiss Ephemeris SweConst.SEFLG_JPLEPH

        int result = swissEph.swe_houses(julianDate, SweConst.SEFLG_SIDEREAL, giornOraPosDTO.getLat(), giornOraPosDTO.getLon(), houseSys, cusps, ascmc);


        for (int i = 0; i < cusps.length; i++) {
            logger.info("Elemento " + i + ": " + cusps[i]);
        }

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



    /**
     * Roma LAT 41.89 e LONG 12.48
     */

    private static final int SID_METHOD = SweConst.SE_SIDM_LAHIRI;

    public ArrayList<CasePlacide> getCasePlacide_OLD(GiornoOraPosizioneDTO giornOraPosDTO) {

        /**
         * The method to determine ayanamsha value:
         */



        System.out.println((int)'P');
        // Input data:
        //		int year = 2013;
        //		int month = 9;
        //		int day = 4;
        //		double longitude = 80 + 17 / 60.0; // Chennai
        //		double latitude = 13 + 5 / 60.0;
        //		double hour = 7 + 30. / 60. - 5.5; // IST
        //AmitabhBacchan http://www.astrosage.com/celebrity-horoscope/amitabh-bachchan-horoscope.asp
        int year = 1993;
        int month = 6;
        int day = 19;
        double longitude = 12.48; //81 + 50 / 60.0; // Allahabad
        double latitude = 41.89; // 25 + 57 / 60.0;
        double hour = 15;// 16 + (0. / 60.) - 5.5; // IST



        // Use ... new SwissEph("/path/to/your/ephemeris/data/files"); when
        // your data files don't reside somewhere in the paths defined in
        // SweConst.SE_EPHE_PATH, which is ".:./ephe:/users/ephe2/:/users/ephe/"
        // currently.
        SwissEph sw = new SwissEph();
        SweDate sd = new SweDate(year, month, day, hour);
        System.out.println(sd.getDate(0).toString());
        // Set sidereal mode:
        sw.swe_set_sid_mode(SID_METHOD, 0, 0);

        // Some required variables:
        double[] cusps = new double[13];
        double[] acsc = new double[10];
        double[] xp = new double[6];
        StringBuffer serr = new StringBuffer();

        // Print input details:
        System.out.println("Date (YYYY/MM/DD): " + sd.getYear() + "/" + sd.getMonth() + "/" + sd.getDay() + ", "
                + toHMS(sd.getHour()));
        System.out.println("Jul. day:  " + sd.getJulDay());
        System.out.println("DeltaT:    " + sd.getDeltaT() * 24 * 3600 + " sec.");
        System.out.println("Location:  " + toDMS(Math.abs(longitude)) + (longitude > 0 ? "E" : "W") + " / "
                + toDMS(Math.abs(latitude)) + (latitude > 0 ? "N" : "S"));

        // Get and print ayanamsa value for info:
        double ayanamsa = sw.swe_get_ayanamsa_ut(sd.getJulDay());
        System.out.println("Ayanamsa:  " + toDMS(ayanamsa) + " (" + sw.swe_get_ayanamsa_name(SID_METHOD) + ")");

        // Get and print lagna:
        int flags = SweConst.SEFLG_SIDEREAL;
        int result = sw.swe_houses(sd.getJulDay(), flags, latitude, longitude, 'P', cusps, acsc);
        System.out.println("Ascendant: " + toDMS(acsc[0]) + "\n");

        int ascSign = (int) (acsc[0] / 30) + 1;

        // Calculate all planets:
        int[] planets = { SweConst.SE_SUN, SweConst.SE_MOON, SweConst.SE_MARS, SweConst.SE_MERCURY, SweConst.SE_JUPITER,
                SweConst.SE_VENUS, SweConst.SE_SATURN, SweConst.SE_TRUE_NODE }; // Some
        // systems
        // prefer
        // SE_MEAN_NODE

        flags = SweConst.SEFLG_SWIEPH | // fastest method, requires data files
                SweConst.SEFLG_SIDEREAL | // sidereal zodiac
                SweConst.SEFLG_NONUT | // will be set automatically for sidereal
                // calculations, if not set here
                SweConst.SEFLG_SPEED; // to determine retrograde vs. direct
        // motion
        int sign;
        int house;
        boolean retrograde = false;

        for (int p = 0; p < planets.length; p++) {
            int planet = planets[p];
            String planetName = sw.swe_get_planet_name(planet);
            int ret = sw.swe_calc_ut(sd.getJulDay(), planet, flags, xp, serr);

            if (ret != flags) {
                if (serr.length() > 0) {
                    System.err.println("Warning: " + serr);
                } else {
                    System.err.println(String.format("Warning, different flags used (0x%x)", ret));
                }
            }

            sign = (int) (xp[0] / 30) + 1;
            house = (sign + 12 - ascSign) % 12 + 1;
            retrograde = (xp[3] < 0);

            System.out.printf("%-12s: %s %c; sign: %2d; %s in house %2d\n", planetName, toDMS(xp[0]),
                    (retrograde ? 'R' : 'D'), sign, toDMS(xp[0] % 30), house);
        }
        // KETU
        xp[0] = (xp[0] + 180.0) % 360;
        String planetName = "Ketu (true)";

        sign = (int) (xp[0] / 30) + 1;
        house = (sign + 12 - ascSign) % 12 + 1;

        System.out.printf("%-12s: %s %c; sign: %2d; %s in house %2d\n", planetName, toDMS(xp[0]),
                (retrograde ? 'R' : 'D'), sign, toDMS(xp[0] % 30), house);



        if (result == SweConst.OK) {
            // Stampare le posizioni delle case
            for (int i = 1; i <= 12; i++) {
                Map.Entry<Integer, String> entry = Util.determinaSegnoZodiacale(cusps[i]).entrySet().iterator().next();

                logger.info("Casa " + i + ": " + cusps[i] +" "+ entry.getValue());
            }
        } else {
            //logger.info("Errore durante il calcolo delle case astrologiche: " + swissEph.swe_get_planet_name(result));
        }




        return null;
    }

    static String toHMS(double d) {
        d += 0.5 / 3600.; // round to one second
        int h = (int) d;
        d = (d - h) * 60;
        int min = (int) d;
        int sec = (int) ((d - min) * 60);



        return String.format("%2d:%02d:%02d", h, min, sec);
    }

    static String toDMS(double d) {
        d += 0.5 / 3600. / 10000.; // round to 1/1000 of a second
        int deg = (int) d;
        d = (d - deg) * 60;
        int min = (int) d;
        d = (d - min) * 60;
        double sec = d;

        return String.format("%3d°%02d'%07.4f\"", deg, min, sec);
    }


    public static double[] convertToZodiacDegrees(double[] data) {
        double min = findMin(data);
        double max = findMax(data);

        double[] result = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            result[i] = ((data[i] - min) / (max - min)) * 360.0;
        }
        return result;
    }

    public static double findMin(double[] data) {
        double min = data[0];
        for (double val : data) {
            if (val < min) {
                min = val;
            }
        }
        return min;
    }

    public static double findMax(double[] data) {
        double max = data[0];
        for (double val : data) {
            if (val > max) {
                max = val;
            }
        }
        return max;
    }






    public  double adjustValue(double value, double subtract) {
        value -= subtract;
        // Normalizza il valore tra 0 e 360
        if (value < 0) {
            value = (value + 360) % 360;
        }
        return value;
    }


    public static double calculateCentralAngle(double angle1, double angle2) {
        double average = (angle1 + angle2) / 2;

        // Normalize to 0-360
        /*
        if (average >= 360) {
            average -= 360;
        }
*/
        return average;
    }

    public static String getZodiacSign(double degree) {
        //degree = degree + 15.0;
        String[] signs = {"Ariete", "Toro", "Gemelli", "Cancro", "Leone", "Vergine", "Bilancia", "Scorpione", "Sagittario", "Capricorno", "Acquario", "Pesci"};
        int signIndex = (int) (degree / 30);
        return signs[signIndex];
    }


}
