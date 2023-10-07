package com.lunasapiens.zodiac;

import com.lunasapiens.Constants;

import java.util.ArrayList;

public class Util {


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
