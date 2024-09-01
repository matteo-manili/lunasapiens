package com.lunasapiens;

import com.lunasapiens.dto.RelationshipOption;

import java.time.format.DateTimeFormatter;
import java.util.*;

public class Constants {

    public static final String DOM_LUNA_SAPIENS = "https://www.lunasapiens.com";

    public static final String PATH_STATIC = "src/main/resources/static/";
    public static final String USER_SESSION_ID = "userSessionId";


    public static final String INFO_MESSAGE = "infoMessage";
    public static final String INFO_ERROR = "infoError";

    public static final String JWT_WITH_ISSUER = "lunaSapiens_jwt_auth0";
    public static final String COOKIE_JWT_NAME = "jwtToken";
    public static final String COOKIE_DISABLED_GOOGLE_ANALYTICS = "cookie_disable_google_analytics";


    public static final String DOM_LUNA_SAPIENS_SUBSCRIBE_OROSC_GIORN = "subscribe";
    public static final String DOM_LUNA_SAPIENS_CONFIRM_EMAIL_OROSC_GIORN = "confirm-email-subscription-orosc-giorn";
    public static final String DOM_LUNA_SAPIENS_CANCELLA_ISCRIZ_OROSC_GIORN = "cancel-email-subscription-orosc-giorn";

    public static final String DOM_LUNA_SAPIENS_SUBSCRIBE_TEMA_NATALE = "subscribe-tema-natale";
    public static final String DOM_LUNA_SAPIENS_CONFIRM_EMAIL_TEMA_NATALE = "confirm-email-subscription-tema-natale";

    public static final DateTimeFormatter DATE_TIME_LOCAL_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    public static final DateTimeFormatter FORMATTER_GIORNO_MESE_ANNO = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ITALIAN);

    public static final int TOO_MANY_REQUESTS_STATUS_CODE = 429; // Codice di stato HTTP per "Too Many Requests"
    public static final String SKIP_EMAIL_SAVE = "skipEmailSave";

    public static final String VIDEO_CACHE = "videoCache";
    public static final String MESSAGE_BOT_CACHE = "messageBotCache";
    public static final String URLS_ASTRO_SEEK_CACHE = "urlsAstroSeekCache";
    public static final String LIMITATORE_MESS_BOT_IA_USER_CACHE = "rateLimiterUserCache";

    public static final List<RelationshipOption> RELATIONSHIP_OPTIONS  = Collections.unmodifiableList(Arrays.asList(
            new RelationshipOption("P", "Partners"),
            new RelationshipOption("G", "Genitore - figlio"),
            new RelationshipOption("F", "Figlio - Genitore"),
            new RelationshipOption("A", "Amici"),
            new RelationshipOption("PR", "Professionale"),
            new RelationshipOption("N", "Preferisco non dirlo")));


    public static final List<String> URL_INDEX_LIST = Collections.unmodifiableList(Arrays.asList(
            "/",
            "/tema-natale",
            "/sinastria",
            "/oroscopo",
            "/omeopatia-astrologica",
            "/forum",
            "/register",
            "/info-privacy",
            "/contatti"));

    public static final List<String> URL_NO_INDEX_STATUS_410_LIST = Collections.unmodifiableList(Arrays.asList(
            "/chat-websocket",
            "/user/queue/reply",
            "/app/message",
            "/coordinate",
            "/private",
            "/header",
            "/greeting",
            "/facebook",
            "/error"));


    public static final String USER = "USER";





    public enum Pianeti {
        SOLE(0, "Sole", "Sun"),
        LUNA(1, "Luna", "Moon"),
        MERCURIO(2, "Mercurio", "Mercury"),
        VENERE(3, "Venere", "Venus"),
        MARTE(4, "Marte", "Mars"),
        GIOVE(5, "Giove", "Jupiter"),
        SATURNO(6, "Saturno", "Saturn"),
        URANO(7, "Urano", "Uranus"),
        NETTUNO(8, "Nettuno", "Neptune"),
        PLUTONE(9, "Plutone", "Pluto");

        private final int numero;
        private final String nome;
        private final String nome_en;

        Pianeti(int numero, String nome, String nome_en) {
            this.numero = numero; this.nome = nome; this.nome_en = nome_en;
        }
        public int getNumero() {
            return numero;
        }
        public String getNome() {
            return nome;
        }
        public String getNomeEn() {
            return nome_en;
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
        public static Pianeti fromNomeEn(String nomeEn) {
            try {
                for (Pianeti pianeta : Pianeti.values()) {
                    if (pianeta.getNomeEn().equalsIgnoreCase(nomeEn)) {
                        return pianeta;
                    }
                }
            } catch (IllegalArgumentException exc) {
                return null;
            }
            return null;
        }
        public static List<Pianeti> getAllPianeti() {
            return Arrays.asList(Pianeti.values());
        }
    }


    public enum Aspetti {
        CONGIUNZIONE(0, "Congiunzione"),
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
            for (Aspetti aspetto : Aspetti.values()) {
                if (aspetto.getCode() == code) {
                    return aspetto;
                }
            }
            throw new IllegalArgumentException("Aspetto Invalid code: " + code);
        }
        public static List<Aspetti> getAllAspetti() {
            return Arrays.asList(Aspetti.values());
        }
    }


    public enum SegniZodiacali {
        ARIETE(0, "Ariete", "Aries", 0.0),
        TORO(1, "Toro", "Taurus", 30.0),
        GEMELLI(2, "Gemelli", "Gemini", 60.0),
        CANCRO(3, "Cancro", "Cancer", 90.0),
        LEONE(4, "Leone", "Leo", 120.0),
        VERGINE(5, "Vergine", "Virgo", 150.0),
        BILANCIA(6, "Bilancia", "Libra", 180.0),
        SCORPIONE(7, "Scorpione", "Scorpio", 210.0),
        SAGITTARIO(8, "Sagittario", "Sagittarius", 240.0),
        CAPRICORNO(9, "Capricorno", "Capricorn", 270.0),
        ACQUARIO(10, "Acquario", "Aquarius", 300.0),
        PESCI(11, "Pesci", "Pisces", 330.0);
        private final int numero;
        private final String nome;
        private final String nome_en;
        private final double gradi;
        SegniZodiacali(int numero, String nome, String nome_en, double gradi) {
            this.numero = numero; this.nome = nome; this.nome_en = nome_en; this.gradi = gradi;
        }
        public int getNumero() { return numero; }
        public String getNome() {
            return nome;
        }
        public String getNomeEn() {
            return nome_en;
        }
        public double getGradi() { return gradi; }
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
        public static SegniZodiacali fromNomeEn(String nomeEn) {
            for (SegniZodiacali segno : SegniZodiacali.values()) {
                if (segno.getNomeEn().equalsIgnoreCase(nomeEn)) {
                    return segno;
                }
            }
            throw new IllegalArgumentException("Nome Pianeta non valido: " + nomeEn);
        }
        public static List<SegniZodiacali> getAllSegniZodiacali() {
            return Arrays.asList(SegniZodiacali.values());
        }
    }



    public enum Elementi {
        FUOCO(0, "Fuoco"),
        ACQUA(1, "Acqua"),
        TERRA(2, "Terra"),
        ARIA(3, "Aria");
        private final int code;
        private final String name;
        Elementi(int code, String name) {
            this.code = code;
            this.name = name;
        }
        public int getCode() {
            return code;
        }
        public String getName() {
            return name;
        }
        public static Elementi fromCode(int code) {
            for (Elementi elemento : Elementi.values()) {
                if (elemento.getCode() == code) {
                    return elemento;
                }
            }
            throw new IllegalArgumentException("Elemento invalid code: " + code);
        }
        public static List<Elementi> getAllElementi() {
            return Arrays.asList(Elementi.values());
        }
    }


    // caratteristiche segno
    public static final List<String> segnoZodiacaleGenere = Arrays.asList("Maschile", "Femminile");
    public static final List<String> segnoZodiacaleNatura = Arrays.asList("Cardinale", "Fisso", "Mobile");


    public enum Case {
        CASA_1(1, "1 AC", "AC"),
        CASA_2(2, "2", "2"),
        CASA_3(3, "3", "3"),
        CASA_4(4, "4 IC", "IC"),
        CASA_5(5, "5", "5"),
        CASA_6(6, "6", "6"),
        CASA_7(7, "7 DC", "DC"),
        CASA_8(8, "8", "8"),
        CASA_9(9, "9", "9"),
        CASA_10(10, "10 MC", "MC"),
        CASA_11(11, "11", "11"),
        CASA_12(12, "12", "12");
        private final int numero;
        private final String name;
        private final String code;
        Case(int numero, String name, String code) {
            this.numero = numero;
            this.name = name;
            this.code = code;
        }
        public int getNumero() {
            return numero;
        }
        public String getName() { return name; }
        public String getCode() { return code; }
        public static Case fromNumero(int numero) {
            for (Case casa : Case.values()) {
                if (casa.getNumero() == numero) {
                    return casa;
                }
            }
            throw new IllegalArgumentException("Invalid numero: " + numero);
        }
        public static Case fromName(String name) {
            for (Case casa : Case.values()) {
                if (casa.getName().equalsIgnoreCase(name)) {
                    return casa;
                }
            }
            throw new IllegalArgumentException("Invalid name: " + name);
        }
        public static Case fromCode(String code) {
            for (Case casa : Case.values()) {
                if (casa.getCode().equalsIgnoreCase(code)) {
                    return casa;
                }
            }
            throw new IllegalArgumentException("Invalid code: " + code);
        }
    }



}
