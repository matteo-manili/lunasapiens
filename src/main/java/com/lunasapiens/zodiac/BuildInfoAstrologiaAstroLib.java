package com.lunasapiens.zodiac;

import at.kugel.zodiac.TextHoroscop;
import at.kugel.zodiac.house.*;
import at.kugel.zodiac.planet.PlanetAA0;
import com.lunasapiens.Constants;
import com.lunasapiens.dto.GiornoOraPosizioneDTO;
import com.lunasapiens.utils.UtilsZodiac;

import java.util.ArrayList;
import java.util.Map;

/**
 * questa classe si interfaccia con la libreria at.kugel.zodiac
 */
@Deprecated
public class BuildInfoAstrologiaAstroLib {

    private String horoscop;
    private GiornoOraPosizioneDTO giornoOraPosizioneDTO;

    // Roma 41.89 e 12.48
    public BuildInfoAstrologiaAstroLib(GiornoOraPosizioneDTO giornoOraPosizioneDTO) {

        this.horoscop = textHoroscop(giornoOraPosizioneDTO.getOra(), giornoOraPosizioneDTO.getMinuti(), giornoOraPosizioneDTO.getGiorno(), giornoOraPosizioneDTO.getMese(),
                giornoOraPosizioneDTO.getAnno(), giornoOraPosizioneDTO.getLon(), giornoOraPosizioneDTO.getLat());

        //OroscopoBase aa = getOroscopoBase();
        //ArrayList<CasePlacide> bb = getCasePlacide();
        //ArrayList<PianetaPosizione> cc = getPianetiAspetti();
    }

    /**
     * inserisce per default la ora a 12.00
     */

    /*
    public BuildInfoAstrologia( int giorno, int mese, int anno, double lon, double lat ) {
        this.ora = 12; this.minuti = 0;
        this.giorno = giorno; this.mese = mese; this.anno = anno; this.lon = lon; this.lat = lat;

        this.horoscop = textHoroscop(ora, minuti, giorno, mese, anno, lon, lat);
        OroscopoBase aa = getOroscopoBase();
        ArrayList<CasePlacide> bb = getCasePlacide();
        ArrayList<PianetiAspetti> cc = getPianetiAspetti();
    }
    */


    public ArrayList<Pianeti> getPianetiAspetti(){
        ArrayList<Pianeti> pianetaArrayList = null;
        // Trova l'indice di inizio e fine di "Planets "AA0""
        int startIndex = horoscop.toString().indexOf("Planets \"AA0\"");
        if (startIndex != -1) {
            int endIndex = horoscop.toString().indexOf("];", startIndex);
            if (endIndex != -1) {
                // Estrai il contenuto tra parentesi quadre di "Planets "AA0""
                String planetsContent = horoscop.toString().substring(startIndex +15, endIndex ); // Aggiungi 2 per includere "];"
                //System.out.println("Contenuto tra parentesi quadre di Planets \"AA0\":");
                //System.out.println(planetsContent);
                // Dividi la stringa in base ai punti e virgola per ottenere le informazioni sui pianeti
                String[] planetInfos = planetsContent.split(";");
                pianetaArrayList = new ArrayList<Pianeti>();
                // Per ogni informazione sul pianeta, estrai le informazioni
                for (String planetInfo : planetInfos) {
                    if (planetInfo.contains(":")) {
                        // Utilizza un'espressione regolare per estrarre i dati
                        String[] parts = planetInfo.split(":");
                        String planetName = parts[0].trim();
                        String[] degreesParts = parts[1].trim().split("°|'|\"");
                        double degrees = Integer.parseInt(degreesParts[0]);
                        int minutes = Integer.parseInt(degreesParts[1]);
                        int seconds = Integer.parseInt(degreesParts[2]);

                        //System.out.println("Pianeta: " + planetName);
                        //System.out.println("Gradi: " + degrees);
                        //System.out.println("Minuti: " + minutes);
                        //System.out.println("Secondi: " + seconds);
                        //System.out.println("segno: " + Util.determinaSegnoZodiacale(degrees));

                        Map.Entry<Integer, String> entry = UtilsZodiac.determinaSegnoZodiacale( degrees ).entrySet().iterator().next();
                        String significatoTransitoPianetaSegno = UtilsZodiac.significatoTransitoPianetaSegno(null,0, entry.getKey());

                        // non valorizzo significatoTransitoPianetaSegno perchè non è implementato in questa classe. Ma solo nella clase BuildInfoAstrologiaSwiss
                        Pianeti pianeta = new Pianeti(0, planetName, degrees, minutes, seconds, entry.getKey(), entry.getValue(), false, significatoTransitoPianetaSegno);
                        pianetaArrayList.add( pianeta );
                    }
                }
            }
        }
        return pianetaArrayList;
    }

    public ArrayList<CasePlacide> getCasePlacide(){
        ArrayList<CasePlacide> casePlacides = null;
        // Trova l'indice di inizio e fine di "Planets "AA0""
        int startIndex = horoscop.toString().indexOf("House \"Placidus\"");
        if (startIndex != -1) {
            int endIndex = horoscop.toString().indexOf("];", startIndex);
            if (endIndex != -1) {
                // Estrai il contenuto tra parentesi quadre di "Planets "AA0""
                String planetsContent = horoscop.toString().substring(startIndex +18, endIndex ); // Aggiungi 2 per includere "];"

                // Dividi la stringa in base ai punti e virgola per ottenere le informazioni sui pianeti
                String[] planetInfos = planetsContent.split(";");

                // Inizializzo l'ArrayList
                casePlacides = new ArrayList<CasePlacide>();

                // Per ogni informazione sul pianeta, estrai le informazioni
                for (String planetInfo : planetInfos) {
                    if (planetInfo.contains(":")) {
                        // Utilizza un'espressione regolare per estrarre i dati
                        String[] parts = planetInfo.split(":");
                        String planetName = parts[0].trim();

                        String[] degreesParts = parts[1].trim().split("°|'|\"");
                        int degrees = Integer.parseInt(degreesParts[0]);
                        int minutes = Integer.parseInt(degreesParts[1]);
                        int seconds = Integer.parseInt(degreesParts[2]);

                        //System.out.println("Casa Placida: " + planetName);
                        //System.out.println("Gradi: " + degrees);
                        //System.out.println("Minuti: " + minutes);
                        //System.out.println("Secondi: " + seconds);
                        //System.out.println("segno: " + SegnoZodiacale.determinaSegnoZodiacale(degrees));

                        Map.Entry<Integer, String> entry = UtilsZodiac.determinaSegnoZodiacale(degrees).entrySet().iterator().next();


                        Constants.Case casa = Constants.Case.fromNumero( Integer.parseInt(planetName) );

                        CasePlacide aa = new CasePlacide(casa.getNumero(), planetName, degrees, minutes, seconds, entry.getKey(), entry.getValue());
                        casePlacides.add(aa);
                    }
                }
            }
        }
        return casePlacides;
    }


    /*
    public OroscopoBase getOroscopoBase(){

        OroscopoBase horoscopoBase = null;

        String jd = extractValue(horoscop, "JD:");
        String f = extractValue(horoscop, "F:");
        String sz = extractValue(horoscop, "SZ:");

        //System.out.println("JD: " + jd);
        //System.out.println("F: " + f);
        //System.out.println("SZ: " + sz);

        String pattern = "(\\d+)°(\\d+)'(\\d+)\"";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(sz);

        if (matcher.find()) {
            int degrees = Integer.parseInt(matcher.group(1));
            int minutes = Integer.parseInt(matcher.group(2));
            int seconds = Integer.parseInt(matcher.group(3));

            //System.out.println("Gradi: " + degrees);
            //System.out.println("Minuti: " + minutes);
            //System.out.println("Secondi: " + seconds);

            horoscopoBase = new OroscopoBase(jd, f, degrees, minutes, seconds, Util.determinaSegnoZodiacale(degrees));
        }
        return horoscopoBase;
    }


     */

    /*
    private static String extractValue(String input, String key) {
        int startIndex = input.indexOf(key);
        if (startIndex != -1) {
            startIndex += key.length();
            int endIndex = input.indexOf(";", startIndex);
            if (endIndex != -1) {
                return input.substring(startIndex, endIndex).trim();
            }
        }
        return "";
    }

     */

    private String textHoroscop(int ora, int minuti, int giorno, int mese, int anno, double lon, double lat){
        // ottiene un'istanza di oroscopo
        // TextHoroscop horoscop = new TextHoroscop ();
        // imposta    l'oroscopo dell'algoritmo di calcolo della posizione del pianeta desiderato . setPlanet ( nuovo PianetaAA0 ());
        // imposta l'algoritmo di calcolo del sistema domestico desiderato
        // può essere qualsiasi cosa dal pacchetto at.kugel.zodiac.house.    oroscopo . setHouse ( nuova CasaPlacidus ());
        // imposta il valore temporale dei dati utente    oroscopo . setTime ( 1 , 12 , 1953 , 20);

        // imposta il valore della posizione dei dati utente    oroscopo . setLocationDegree (- 16 - 17.0 / 60 , 48 + 4.0 / 60 );
        // calcola i valori    dell'oroscopo . valoricalc ();
        // fa qualcosa con i dati, ad esempio genera dati grezzi System . fuori . println ( oroscopo . toString ());


        // get a horoscop instance
        final TextHoroscop horoscop = new TextHoroscop();
        // set your desired planet position calculation algorithm
        horoscop.setPlanet(new PlanetAA0());

        // set your desired house system calculation algorithm
        // may be anything from the at.kugel.zodiac.house package.
        //horoscop.setHouse(new HousePlacidus());
        horoscop.setHouse(new HousePlacidus());



        // set your user data time value
        double orario = (ora + (minuti / 60.0)) / 24.0;
        horoscop.setTime(giorno, mese, anno, orario);
        // set your user data location value
        horoscop.setLocationDegree(lon, lat);
        // calculate the values
        horoscop.calcValues();


        // do something with the data or output raw data
        System.out.println("START: "+horoscop.toString());

        return horoscop.toString();
    }


    public GiornoOraPosizioneDTO getGiornoOraPosizioneDTO() {
        return giornoOraPosizioneDTO;
    }

    public void setGiornoOraPosizioneDTO(GiornoOraPosizioneDTO giornoOraPosizioneDTO) {
        this.giornoOraPosizioneDTO = giornoOraPosizioneDTO;
    }
}
