package at.kugel.zodiac.util;

public class SegnoZodiacale {


    public static String determinaSegnoZodiacale(int grado) {
        if (grado >= 0 && grado < 30) {
            return "Ariete";
        } else if (grado >= 30 && grado < 60) {
            return "Toro";
        } else if (grado >= 60 && grado < 90) {
            return "Gemelli)";
        } else if (grado >= 90 && grado < 120) {
            return "Cancro";
        } else if (grado >= 120 && grado < 150) {
            return "Leone";
        } else if (grado >= 150 && grado < 180) {
            return "Vergine";
        } else if (grado >= 180 && grado < 210) {
            return "Bilancia";
        } else if (grado >= 210 && grado < 240) {
            return "Scorpione";
        } else if (grado >= 240 && grado < 270) {
            return "Sagittario";
        } else if (grado >= 270 && grado < 300) {
            return "Capricorno";
        } else if (grado >= 300 && grado < 330) {
            return "Acquario";
        } else if (grado >= 330 && grado <= 360) {
            return "Pesci";
        } else {
            return "Grado non valido";
        }
    }




}
