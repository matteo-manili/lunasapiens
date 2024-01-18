package com.lunasapiens.zodiac;

import com.lunasapiens.Constants;
import com.lunasapiens.dto.GiornoOraPosizioneDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Util {


    // Roma 49.9 e 12.4 --- Pisa 43.7 e 10.4

    public static final GiornoOraPosizioneDTO GiornoOraPosizione_OggiRomaOre12(){


        // ufffff

        LocalDateTime now = LocalDateTime.now();

        GiornoOraPosizioneDTO giornoOraPosizioneDTO = new GiornoOraPosizioneDTO(12, 0, now.getDayOfMonth(),
                now.getMonthValue(), now.getYear(), 49.9, 12.4);


        return giornoOraPosizioneDTO;
    }










    public static String determinaSegnoZodiacale(int grado) {
        if (grado >= 0 && grado < 30) {
            return Constants.segniZodiacali().get(0);
        } else if (grado >= 30 && grado < 60) {
            return Constants.segniZodiacali().get(1);
        } else if (grado >= 60 && grado < 90) {
            return Constants.segniZodiacali().get(2);
        } else if (grado >= 90 && grado < 120) {
            return Constants.segniZodiacali().get(3);
        } else if (grado >= 120 && grado < 150) {
            return Constants.segniZodiacali().get(4);
        } else if (grado >= 150 && grado < 180) {
            return Constants.segniZodiacali().get(5);
        } else if (grado >= 180 && grado < 210) {
            return Constants.segniZodiacali().get(6);
        } else if (grado >= 210 && grado < 240) {
            return Constants.segniZodiacali().get(7);
        } else if (grado >= 240 && grado < 270) {
            return Constants.segniZodiacali().get(8);
        } else if (grado >= 270 && grado < 300) {
            return Constants.segniZodiacali().get(9);
        } else if (grado >= 300 && grado < 330) {
            return Constants.segniZodiacali().get(10);
        } else if (grado >= 330 && grado <= 360) {
            return Constants.segniZodiacali().get(11);
        } else {
            return "Grado non valido";
        }
    }




}
