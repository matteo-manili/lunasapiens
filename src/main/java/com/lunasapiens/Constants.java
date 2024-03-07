package com.lunasapiens;

import java.util.ArrayList;

public class Constants {

    public static final String VIDEO_CACHE = "videoCache";

    public static final String SeparatoreTestoOroscopo = "#@#";

    public static final String PATH_STATIC = "src/main/resources/static/";

    public static String getSegnoZodiacale(int numero) {
        ArrayList<String> segniZodiacali = segniZodiacali();
        if (numero >= 1 && numero <= segniZodiacali.size()) {
            return segniZodiacali.get(numero - 1);
        } else {
            return null;
        }
    }








    public static final String[] NAME_PLANET = new String[] {
            "Sole", "Luna", "Mercurio", "Venere", "Marte", "Giove", "Saturno", "Urano", "Nettuno", "Plutone"
    };

    public static ArrayList<String> segniZodiacali(){
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



}
