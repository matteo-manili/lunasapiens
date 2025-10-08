package com.lunasapiens;

import com.lunasapiens.config.JwtElements;
import com.lunasapiens.dto.GiornoOraPosizioneDTO;
import de.thmac.swisseph.SweDate;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

public class Utils {

    private static final Logger logger = LoggerFactory.getLogger(Utils.class);




    public static List<String> extractImageNames(String htmlContent) {
        List<String> imageNames = new ArrayList<>();
        try {
            // Analizza l'HTML
            Document document = Jsoup.parse(htmlContent);
            // Seleziona tutti gli elementi <img> nel documento
            Elements imgElements = document.select("img");
            for (Element img : imgElements) {
                // Estrai il valore dell'attributo "src"
                String imgSrc = img.attr("src");
                // Estrai solo il nome del file (senza il percorso)
                String fileName = imgSrc.substring(imgSrc.lastIndexOf("/") + 1);
                // Aggiungi il nome del file alla lista
                imageNames.add(fileName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imageNames;
    }


    public static void creaCookieTokenJwt(HttpServletResponse response, JwtElements.JwtDetails jwtDetails){
        // Creazione del cookie con il token JWT
        Cookie cookie = new Cookie(Constants.COOKIE_LUNASAPIENS_AUTH_TOKEN, jwtDetails.getToken());
        cookie.setHttpOnly(true); // Imposta il cookie come HttpOnly per evitare accessi lato client
        cookie.setSecure(true); // Imposta il cookie come sicuro per inviarlo solo su HTTPS
        cookie.setPath("/"); // Imposta il percorso del cookie
        //jwtCookie.setMaxAge(24 * 60 * 60); // Imposta la durata del cookie (es. 24 ore)
        //cookie.setMaxAge(7 * 24 * 60 * 60); // Imposta la durata del cookie a 7 giorni (604800 secondi)
        cookie.setMaxAge(365 * 24 * 60 * 60); // Imposta la durata del cookie a 1 anno (31536000 secondi)
        // Aggiungi il cookie alla risposta HTTP
        response.addCookie(cookie);
        logger.info("creato cookie jwt per l'utente: "+jwtDetails.getSubject());
    }

    public static void clearJwtCookie_ClearSecurityContext(HttpServletRequest request, HttpServletResponse response) {
        logger.info("cancello Cookie JWT e ClearSecurityContext");
        // Cancella il cookie JWT
        Cookie jwtCookie = new Cookie(Constants.COOKIE_LUNASAPIENS_AUTH_TOKEN, null);
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

    public static boolean isPresentCookieDisabledGoogleAnalytics(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        // Se non ci sono cookie nella richiesta, ritorna false
        if (cookies == null) {
            return false;
        }
        // Cerca il cookie con il nome specifico
        for (Cookie cookie : cookies) {
            if (Constants.COOKIE_DISABLED_GOOGLE_ANALYTICS.equals(cookie.getName())) {
                // Controlla se il valore del cookie è "true"
                return "true".equals(cookie.getValue());
            }
        }
        return false;
    }


    public static double convertiGiornoOraPosizioneDTO_in_JulianDate(GiornoOraPosizioneDTO giornOraPosDTO) {
        double hour = giornOraPosDTO.getOra() + (giornOraPosDTO.getMinuti() / 60.0);
        return SweDate.getJulDay(giornOraPosDTO.getAnno(), giornOraPosDTO.getMese(), giornOraPosDTO.getGiorno(), hour, true);
    }



    public static ZoneId getZoneIdRomeEurope() {
        return ZoneId.of("Europe/Rome");
    }

    public static ZonedDateTime getNowRomeEurope() { return ZonedDateTime.now( getZoneIdRomeEurope() ); }


    public static LocalDateTime OggiRomaOre12() {
        ZonedDateTime zdtRome = ZonedDateTime.now( getZoneIdRomeEurope() )
                .withHour(12).withMinute(0).withSecond(0).withNano(0);
        return zdtRome.toLocalDateTime();
    }


    public static LocalDateTime OggiRomaOre0() {
        ZonedDateTime zdtRome = ZonedDateTime.now( getZoneIdRomeEurope() )
                .withHour(0).withMinute(0).withSecond(0).withNano(0);
        return zdtRome.toLocalDateTime();
    }


    public static Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone( getZoneIdRomeEurope() ).toInstant());
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



    public static LocalDateTime convertiGiornoOraPosizioneDTOInLocalDateTime(GiornoOraPosizioneDTO dto) {
        LocalDateTime localDateTime = LocalDateTime.of(
                dto.getAnno(),
                dto.getMese(),
                dto.getGiorno(),
                dto.getOra(),
                dto.getMinuti(),
                0  // secondi
        );
        logger.info("convertiGiornoOraPosizioneDTOInLocalDateTime: " + localDateTime);
        return localDateTime;
    }


    /**
     * Pulisce il testo rimuovendo HTML, spazi extra e convertendo tutto in minuscolo.
     *
     * 🔹 Perché serve:
     *    Garantisce che gli embedding calcolati siano coerenti e privi di rumore
     *    (tag HTML, spazi multipli, lettere maiuscole) prima di passare il testo
     *    al modello di embedding o salvarlo nel database.
     *
     * @param content testo originale
     * @return testo pulito pronto per embedding o elaborazioni successive
     */
    public static String cleanHtmlText(String content) {
        if (content == null) return "";
        return Jsoup.parse(content).text().toLowerCase().trim();
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
