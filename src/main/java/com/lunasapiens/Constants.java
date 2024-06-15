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
            return "Indice Pianeta non valido";
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

    // caratteristiche segno
    public static final List<String> segnoZodiacaleGenere = Arrays.asList("Maschile", "Femminile");
    public static final List<String> segnoZodiacaleNatura = Arrays.asList("Cardinale", "Fisso", "Mobile");
    public static final List<String> segnoZodiacaleElemento = Arrays.asList("Fuoco", "Terra", "Aria", "Acqua");



    public enum Aspetti {
        CONGIUNZIONE(0, "Congiunzine"),
        SESTILE(1, "Sestile"),
        QUADRATO(2, "Quadrato"),
        TRIGONO(3, "Trigono"),
        OPPOSIZIONE(4, "Opposizione"),
        QUINCUNX(5, "Quincunx");

        private final int code;
        private final String name;

        Aspetti(int code, String name) {
            this.code = code;
            this.name = name;
        }

        public int getCode() {
            return code;
        }

        public String getName() {
            return name;
        }

        public static Aspetti fromCode(int code) {
            for (Aspetti aspetti : Aspetti.values()) {
                if (aspetti.getCode() == code) {
                    return aspetti;
                }
            }
            throw new IllegalArgumentException("Invalid code: " + code);
        }
    }



}
