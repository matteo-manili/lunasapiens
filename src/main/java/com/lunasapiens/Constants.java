package com.lunasapiens;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Constants {

    public static final String VIDEO_CACHE = "videoCache";
    public static final String PATH_STATIC = "src/main/resources/static/";




    public static final String[] Pianeti = new String[] {
            "Sole", "Luna", "Mercurio", "Venere", "Marte", "Giove", "Saturno", "Urano", "Nettuno", "Plutone"
    };



    public static String Pianeta(int index) {
        // Controlla se l'indice è valido
        if (index >= 0 && index < Pianeti.length) {
            return Pianeti[index];
        } else {
            // Restituisce un messaggio di errore se l'indice non è valido
            return "Indice non valido";
        }
    }



    public static ArrayList<String> segniZodiacali() {
        ArrayList<String> segniZodiacali = new ArrayList<>();
        segniZodiacali.add("Ariete");
        segniZodiacali.add("Toro");
        segniZodiacali.add("Gemelli");
        segniZodiacali.add("Cancro");
        segniZodiacali.add("Leone");
        segniZodiacali.add("Vergine");
        segniZodiacali.add("Bilancia");
        segniZodiacali.add("Scorpione");
        segniZodiacali.add("Sagittario");
        segniZodiacali.add("Capricorno");
        segniZodiacali.add("Acquario");
        segniZodiacali.add("Pesci");
        return segniZodiacali;
    }

    public static String segnoZodiacale(int numeroSegno) {
        ArrayList<String> segni = segniZodiacali();
        if (numeroSegno >= 0 && numeroSegno < segni.size()) {
            return segni.get(numeroSegno);
        } else {
            return "Numero segno non valido";
        }
    }

    public static final List<String> segnoZodiacaleGenere = Arrays.asList("Maschile", "Femminile");
    public static final List<String> segnoZodiacaleNatura = Arrays.asList("Cardinale", "Fisso", "Mobile");
    public static final List<String> segnoZodiacaleElemento = Arrays.asList("Fuoco", "Terra", "Aria", "Acqua");





}
