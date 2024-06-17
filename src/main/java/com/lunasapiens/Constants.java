package com.lunasapiens;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Constants {

    public static final String VIDEO_CACHE = "videoCache";
    public static final String PATH_STATIC = "src/main/resources/static/";

    // caratteristiche segno
    public static final List<String> segnoZodiacaleGenere = Arrays.asList("Maschile", "Femminile");
    public static final List<String> segnoZodiacaleNatura = Arrays.asList("Cardinale", "Fisso", "Mobile");
    public static final List<String> segnoZodiacaleElemento = Arrays.asList("Fuoco", "Terra", "Aria", "Acqua");

    public enum Pianeti {
        SOLE(0, "Sole"),
        LUNA(1, "Luna"),
        MERCURIO(2, "Mercurio"),
        VENERE(3, "Venere"),
        MARTE(4, "Marte"),
        GIOVE(5, "Giove"),
        SATURNO(6, "Saturno"),
        URANO(7, "Urano"),
        NETTUNO(8, "Nettuno"),
        PLUTONE(9, "Plutone");
        private final int numero;
        private final String nome;
        Pianeti(int numero, String nome) {
            this.numero = numero;
            this.nome = nome;
        }
        public int getNumero() {
            return numero;
        }
        public String getNome() {
            return nome;
        }
        public static Pianeti fromNumero(int numero) {
            for (Pianeti pianeta : Pianeti.values()) {
                if (pianeta.getNumero() == numero) {
                    return pianeta;
                }
            }
            throw new IllegalArgumentException("Indice Pianeta non valido: " + numero);
        }
        public static List<String> getAllNomi() {
            List<String> nomi = new ArrayList<>();
            for (Pianeti pianeta : Pianeti.values()) {
                nomi.add(pianeta.getNome());
            }
            return nomi;
        }
    }



    public enum SegniZodiacali {
        ARIETE(0, "Ariete"),
        TORO(1, "Toro"),
        GEMELLI(2, "Gemelli"),
        CANCRO(3, "Cancro"),
        LEONE(4, "Leone"),
        VERGINE(5, "Vergine"),
        BILANCIA(6, "Bilancia"),
        SCORPIONE(7, "Scorpione"),
        SAGITTARIO(8, "Sagittario"),
        CAPRICORNO(9, "Capricorno"),
        ACQUARIO(10, "Acquario"),
        PESCI(11, "Pesci");
        private final int numero;
        private final String nome;
        SegniZodiacali(int numero, String nome) {
            this.numero = numero;
            this.nome = nome;
        }
        public int getNumero() {
            return numero;
        }
        public String getNome() {
            return nome;
        }
        public static SegniZodiacali fromNumero(int numero) {
            for (SegniZodiacali segno : SegniZodiacali.values()) {
                if (segno.getNumero() == numero) {
                    return segno;
                }
            }
            throw new IllegalArgumentException("Invalid numero: " + numero);
        }
        public static List<String> getAllNomi() {
            List<String> nomi = new ArrayList<>();
            for (SegniZodiacali segno : SegniZodiacali.values()) {
                nomi.add(segno.getNome());
            }
            return nomi;
        }
    }



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
