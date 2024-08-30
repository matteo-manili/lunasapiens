package com.lunasapiens;

import com.lunasapiens.dto.GiornoOraPosizioneDTO;
import de.thmac.swisseph.SweDate;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

public class Utils {

    private static final Logger logger = LoggerFactory.getLogger(Utils.class);


    public static void clearJwtCookie_ClearSecurityContext(HttpServletRequest request, HttpServletResponse response) {
        logger.info("cancello Cookie JWT e ClearSecurityContext");
        // Cancella il cookie JWT
        Cookie jwtCookie = new Cookie(Constants.COOKIE_JWT_NAME, null);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0); // Scadenza immediata
        response.addCookie(jwtCookie);
        ClearSecurityContext(request, response);
    }

    public static void ClearSecurityContext(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            // Esegue il logout dell'utente
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        /*
        // Disconnetti l'utente dal contesto di sicurezza
        SecurityContextHolder.clearContext();
        // Invalidare la sessione (se presente)
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        */
    }



    public static double convertiGiornoOraPosizioneDTO_in_JulianDate(GiornoOraPosizioneDTO giornOraPosDTO) {
        double hour = giornOraPosDTO.getOra() + (giornOraPosDTO.getMinuti() / 60.0);
        return SweDate.getJulDay(giornOraPosDTO.getAnno(), giornOraPosDTO.getMese(), giornOraPosDTO.getGiorno(), hour, true);
    }


    public static ZonedDateTime getNowRomeEurope() {
        ZoneId romaZone = ZoneId.of("Europe/Rome");
        ZonedDateTime now = ZonedDateTime.now(romaZone);
        logger.info("ZonedDateTime Roma_:" +now);
        return now;
    }


    public static Date OggiOre12() {
        ZonedDateTime now = getNowRomeEurope(); Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, now.getDayOfMonth());
        calendar.set(Calendar.MONTH, now.getMonthValue()-1);
        calendar.set(Calendar.YEAR, now.getYear());
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0); calendar.set(Calendar.SECOND, 0); calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * Roma LAT 41.89 e LONG 12.48
     */
    public static GiornoOraPosizioneDTO GiornoOraPosizione_OggiRomaOre12() {
        ZonedDateTime now = getNowRomeEurope();
        GiornoOraPosizioneDTO giornoOraPosizioneDTO = new GiornoOraPosizioneDTO(12, 0, now.getDayOfMonth(),
                now.getMonthValue(), now.getYear(), 41.89, 12.48);
        return giornoOraPosizioneDTO;
    }

    public static GiornoOraPosizioneDTO GiornoOraPosizione_Custom() {
        ZonedDateTime now = getNowRomeEurope();
        ZonedDateTime domani = now.plusDays(1);
        GiornoOraPosizioneDTO giornoOraPosizioneDTO = new GiornoOraPosizioneDTO(3, 0, 25,
                5, 1981, 41.89, 12.48);
        return giornoOraPosizioneDTO;
    }


    public static Date convertiGiornoOraPosizioneDTOInDate(GiornoOraPosizioneDTO giornoOraPosizioneDTO) {
        // Creare un oggetto Calendar e impostare i valori
        Calendar calendar = Calendar.getInstance();
        calendar.set(giornoOraPosizioneDTO.getAnno(), giornoOraPosizioneDTO.getMese()-1, giornoOraPosizioneDTO.getGiorno(), giornoOraPosizioneDTO.getOra(),
                giornoOraPosizioneDTO.getMinuti()); // I secondi sono impostati a 0
        // Impostare i millisecondi e secondi a 0
        calendar.set(Calendar.SECOND, 0); calendar.set(Calendar.MILLISECOND, 0);
        // Ottenere l'oggetto Date dal Calendar
        return calendar.getTime();
    }


    /**
     * Converte l'HTML in testo normale con formattazione specifica:
     * - Rimuove i tag <b> ma mantiene il testo al loro interno.
     * - Sostituisce i tag <br> con nuove righe.
     * - Aggiunge una riga vuota prima e dopo i tag <h4>.
     * - Aggiunge una riga vuota prima e dopo i tag <ul> e <li>.
     *
     * @param html La stringa HTML da convertire.
     * @return La rappresentazione in testo normale dell'HTML.
     */
    public static String convertHtmlToPlainText(String html) {
        Document document = Jsoup.parse(html);
        document.select("h4").prepend("\\n\\n");
        document.select("p").append("\\n");
        document.select("b").unwrap();
        document.select("br").append("\\n");
        document.select("ul").unwrap();
        //document.select("ul").prepend("\\n").append("\\n");
        document.select("li").append("\\n");
        return document.text().replace("\\n", "\n");
    }

    public static String convertPlainTextToHtml(String text) {
        return text.replaceAll("\\n", "<br>");
    }





    /**
     * In sintesi, questo metodo prepara e restituisce una risposta HTTP che contiene un video sotto forma di ByteArrayResource, con le appropriate
     * intestazioni HTTP che descrivono il contenuto della risposta. Questo può essere utile per fornire file video ai client in modo efficiente.
     * @param videoBytes
     * @return
     */
    public static ResponseEntity<ByteArrayResource> VideoResponseEntityByteArrayResource(byte[] videoBytes) {
        ByteArrayResource resource = new ByteArrayResource(videoBytes);
        // Creazione delle intestazioni HTTP
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentLength(videoBytes.length);
        // Creazione dell'oggetto ResponseEntity contenente la risorsa ByteArrayResource e le intestazioni
        ResponseEntity<ByteArrayResource> responseEntity = ResponseEntity.ok()
                .headers(headers)
                .body(resource);
        return responseEntity;
    }


    /**
     * Metodo per determinare il segno zodiacale in base al grado
     * @param grado
     * @return
     */
    public static Map<Integer, String> determinaSegnoZodiacale(double grado) {
        Map<Integer, String> segniZodiacali = new HashMap<>();
        List<String> nomiSegni = Constants.SegniZodiacali.getAllNomi();
        if (grado >= Constants.SegniZodiacali.ARIETE.getGradi() && grado < 30.0d) {                 // Ariete
            segniZodiacali.put(0, nomiSegni.get(0));
        } else if (grado >= Constants.SegniZodiacali.TORO.getGradi() && grado < 60.0d) {         // Toro
            segniZodiacali.put(1, nomiSegni.get(1));
        } else if (grado >= Constants.SegniZodiacali.GEMELLI.getGradi() && grado < 90.0d) {         // Gemelli
            segniZodiacali.put(2, nomiSegni.get(2));
        } else if (grado >= Constants.SegniZodiacali.CANCRO.getGradi() && grado < 120.0d) {        // Cancro
            segniZodiacali.put(3, nomiSegni.get(3));
        } else if (grado >= Constants.SegniZodiacali.LEONE.getGradi() && grado < 150.0d) {       // Leone
            segniZodiacali.put(4, nomiSegni.get(4));
        } else if (grado >= Constants.SegniZodiacali.VERGINE.getGradi() && grado < 180.0d) {       // Vergine
            segniZodiacali.put(5, nomiSegni.get(5));
        } else if (grado >= Constants.SegniZodiacali.BILANCIA.getGradi() && grado < 210.0d) {       // Bilancia
            segniZodiacali.put(6, nomiSegni.get(6));
        } else if (grado >= Constants.SegniZodiacali.SCORPIONE.getGradi() && grado < 240.0d) {       // Scorpione
            segniZodiacali.put(7, nomiSegni.get(7));
        } else if (grado >= Constants.SegniZodiacali.SAGITTARIO.getGradi() && grado < 270.0d) {       // Sagittario
            segniZodiacali.put(8, nomiSegni.get(8));
        } else if (grado >= Constants.SegniZodiacali.CAPRICORNO.getGradi() && grado < 300.0d) {       // Capricorno
            segniZodiacali.put(9, nomiSegni.get(9));
        } else if (grado >= Constants.SegniZodiacali.ACQUARIO.getGradi() && grado < 330.0d) {       // Acquario
            segniZodiacali.put(10, nomiSegni.get(10));
        } else if (grado >= Constants.SegniZodiacali.PESCI.getGradi() && grado < 360.0d) {       // Pesci
            segniZodiacali.put(11, nomiSegni.get(11));
        } else {
            segniZodiacali.put(-1, "Grado non valido");
        }
        return segniZodiacali;
    }




    public static String significatoTransitoPianetaSegno(Properties properties, int numero1, int numero2) {
        // Costruisci la chiave per recuperare il valore desiderato
        String chiaveProperties = numero1 + "_" + numero2;
        // Recupera il significato del pianeta
        String significato = properties.getProperty(chiaveProperties);
        return significato;
    }



    public static void createDirectory(String pathDirectory) {
        File outputFolder = new File(pathDirectory);
        if (!outputFolder.exists()) {
            outputFolder.mkdirs(); // Crea la cartella e tutte le sue sottocartelle se non esiste
            //logger.info("La cartella o il file NON esistono!");
        }else{
            //logger.info("La cartella o il file esistono!");
        }
    }


    public static void eliminaCartelleEFile(String pathOroscopoGiornalieroImmagini) {
        File directoryImmagini = new File(pathOroscopoGiornalieroImmagini);
        deleteDirectory(directoryImmagini);
        File directoryVideo = new File(GeneratorVideo.pathOroscopoGiornalieroVideo);
        deleteDirectory(directoryVideo);
    }


    public static void deleteDirectory(File directory) {
        File[] contents = directory.listFiles();
        if (contents != null) {
            for (File file : contents) {
                if (file.isDirectory()) {
                    // Se è una cartella, elimina ricorsivamente i suoi contenuti
                    deleteDirectory(file);
                } else {
                    // Se è un file, elimina il file
                    file.delete();
                }
            }
        }
        // Dopo aver eliminato tutti i contenuti, elimina la cartella stessa
        directory.delete();
    }




    public static List<String> loadPropertiesEsternoLunaSapiens(List<String> keysProperties ) {
        List<String> keysPropertiesResult = new ArrayList<>();
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream("C:/intellij_work/lunasapiens-application-db.properties")) {
            properties.load(fis);
            for (String str : keysProperties) {
                //logger.info("keysProperties: "+str);
                keysPropertiesResult.add( properties.getProperty( str ) );
            }
        } catch (IOException ioException) {
            throw new RuntimeException("Errore nella lettura del file di configurazione esterno.", ioException);
        }
        return keysPropertiesResult;
    }


    public static boolean isLocalhost() {
        try {
            InetAddress localhost = InetAddress.getLocalHost();
            System.out.println("Nome host: " + localhost);

            // cambia a ogni deploy
            // in produzione viene Nome host: 0678b9f9-9ded-4ad1-967e-ac3663bd743a/172.18.13.50
            //                                13d614cc-dcb5-4d8b-b33e-b60fa1cf12d0/172.18.117.170

            if( localhost.toString().contains("DESKTOP-MATTEO") ){
                System.out.println("Ambiente rilevato: DEV");
                return true;
            }else{
                System.out.println("Ambiente rilevato: PROD");
                return false;
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return true; // Ritorna true se si verifica un'eccezione (es. per sicurezza in sviluppo)
        }
    }


}
