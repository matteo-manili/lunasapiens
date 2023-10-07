package com.lunasapiens;

import java.util.ArrayList;

public class Constants {


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
