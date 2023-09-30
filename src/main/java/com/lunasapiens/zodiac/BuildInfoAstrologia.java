package com.lunasapiens.zodiac;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BuildInfoAstrologia {

    private ArrayList<PianetiAspetti> pianetiAspetti = null;
    private ArrayList<CasePlacide> casePlacides = null;
    private OroscopoBase horoscopoBase = null;


    public OroscopoBase setOroscopoBase(String horoscop){

        //System.out.println("############################ setOroscopoBase ###################################");

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

            horoscopoBase = new OroscopoBase(jd, f, degrees, minutes, seconds, SegniZodiacali.determinaSegnoZodiacale(degrees));
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




    public ArrayList<CasePlacide> setCasePlacide(String horoscop){

        // Trova l'indice di inizio e fine di "Planets "AA0""
        int startIndex = horoscop.toString().indexOf("House \"Placidus\"");

        if (startIndex != -1) {
            int endIndex = horoscop.toString().indexOf("];", startIndex);
            if (endIndex != -1) {
                // Estrai il contenuto tra parentesi quadre di "Planets "AA0""
                String planetsContent = horoscop.toString().substring(startIndex +18, endIndex ); // Aggiungi 2 per includere "];"
                //System.out.println("Contenuto tra parentesi quadre di Planets \"AA0\":");
                //System.out.println(planetsContent);

                //----------------------------
                //System.out.println("############################ setCasePlacide ###################################");

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

                        CasePlacide aa = new CasePlacide(planetName, degrees, minutes, seconds, SegniZodiacali.determinaSegnoZodiacale(degrees));
                        casePlacides.add(aa);
                    }
                }
            }
        }
        return casePlacides;
    }




    public ArrayList<PianetiAspetti> setPianetiAspetti(String horoscop){

        // Trova l'indice di inizio e fine di "Planets "AA0""
        int startIndex = horoscop.toString().indexOf("Planets \"AA0\"");
        if (startIndex != -1) {
            int endIndex = horoscop.toString().indexOf("];", startIndex);
            if (endIndex != -1) {
                // Estrai il contenuto tra parentesi quadre di "Planets "AA0""
                String planetsContent = horoscop.toString().substring(startIndex +15, endIndex ); // Aggiungi 2 per includere "];"
                //System.out.println("Contenuto tra parentesi quadre di Planets \"AA0\":");
                //System.out.println(planetsContent);

                //----------------------------
                //System.out.println("############################ setPianetiAspetti ###################################");

                // Dividi la stringa in base ai punti e virgola per ottenere le informazioni sui pianeti
                String[] planetInfos = planetsContent.split(";");

                // Inizializzo l'ArrayList
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

                        PianetiAspetti aa = new PianetiAspetti(planetName, degrees, minutes, seconds, SegniZodiacali.determinaSegnoZodiacale(degrees));
                        pianetiAspetti.add(aa);
                    }
                }
            }
        }

        return pianetiAspetti;
    }





}
