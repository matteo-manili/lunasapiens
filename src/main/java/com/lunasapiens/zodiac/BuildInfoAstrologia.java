package com.lunasapiens.zodiac;

import at.kugel.zodiac.TextHoroscop;
import at.kugel.zodiac.house.HousePlacidus;
import at.kugel.zodiac.planet.PlanetAA0;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BuildInfoAstrologia {


    private String horoscop;

    private int ora;
    private int minuti;
    private int giorno;
    private int mese;
    private int anno;
    private double lon;
    private double lat;

    // Roma 49.9 e 12.4 --- Pisa 43.7 e 10.4

    public BuildInfoAstrologia( int ora, int minuti, int giorno, int mese, int anno, double lon, double lat ) {

        this.ora = ora; this.minuti = minuti;
        this.giorno = giorno; this.mese = mese; this.anno = anno;
        this.lon = lon; this.lat = lat;

        this.horoscop = textHoroscop(ora, minuti, giorno, mese, anno, lon, lat);
        OroscopoBase aa = getOroscopoBase();
        ArrayList<CasePlacide> bb = getCasePlacide();
        ArrayList<PianetiAspetti> cc = getPianetiAspetti();
    }

    /**
     * inserisce per default la ora a 12.00
     */
    public BuildInfoAstrologia( int giorno, int mese, int anno, double lon, double lat ) {
        this.ora = 12; this.minuti = 0;
        this.giorno = giorno; this.mese = mese; this.anno = anno; this.lon = lon; this.lat = lat;

        this.horoscop = textHoroscop(ora, minuti, giorno, mese, anno, lon, lat);
        OroscopoBase aa = getOroscopoBase();
        ArrayList<CasePlacide> bb = getCasePlacide();
        ArrayList<PianetiAspetti> cc = getPianetiAspetti();
    }



    public ArrayList<PianetiAspetti> getPianetiAspetti(){
        ArrayList<PianetiAspetti> pianetiAspetti = null;
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
                pianetiAspetti = new ArrayList<PianetiAspetti>();
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

                        //System.out.println("Pianeta: " + planetName);
                        //System.out.println("Gradi: " + degrees);
                        //System.out.println("Minuti: " + minutes);
                        //System.out.println("Secondi: " + seconds);
                        //System.out.println("segno: " + SegnoZodiacale.determinaSegnoZodiacale(degrees));

                        PianetiAspetti aa = new PianetiAspetti(planetName, degrees, minutes, seconds, Util.determinaSegnoZodiacale(degrees));
                        pianetiAspetti.add(aa);
                    }
                }
            }
        }
        return pianetiAspetti;
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

                        CasePlacide aa = new CasePlacide(planetName, degrees, minutes, seconds, Util.determinaSegnoZodiacale(degrees));
                        casePlacides.add(aa);
                    }
                }
            }
        }
        return casePlacides;
    }

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
        horoscop.setHouse(new HousePlacidus());
        // set your user data time value
        double orario = (ora + (minuti / 60.0)) / 24.0;
        horoscop.setTime(giorno, mese, anno, orario);
        // set your user data location value
        // Pisa 43.7 e 10.4 --- Roma 49.9 e 12.4
        horoscop.setLocationDegree(lon, lat);
        // calculate the values
        horoscop.calcValues();
        // do something with the data or output raw data
        System.out.println(horoscop.toString());

        return horoscop.toString();
    }


    public int getOra() {
        return ora;
    }

    public int getMinuti() {
        return minuti;
    }

    public int getGiorno() {
        return giorno;
    }

    public int getMese() {
        return mese;
    }

    public int getAnno() {
        return anno;
    }

    public double getLon() {
        return lon;
    }

    public double getLat() {
        return lat;
    }

}