package com.lunasapiens;

import com.lunasapiens.dto.GiornoOraPosizioneDTO;
import de.thmac.swisseph.SweDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

public class Util {

    private static final Logger logger = LoggerFactory.getLogger(Util.class);


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
     * Roma 41.89 e 12.48
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
                5, 1981, -41.9, -12.516);
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


    public static void createDirectory(String pathDirectory) {
        File outputFolder = new File(pathDirectory);
        if (!outputFolder.exists()) {
            outputFolder.mkdirs(); // Crea la cartella e tutte le sue sottocartelle se non esiste
            //logger.info("La cartella o il file NON esistono!");
        }else{
            //logger.info("La cartella o il file esistono!");
        }
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
        ArrayList<String> nomiSegni = Constants.segniZodiacali();
        if (grado >= 0 && grado < 30) {                 // Ariete
            segniZodiacali.put(0, nomiSegni.get(0));
        } else if (grado >= 30 && grado < 60) {         // Toro
            segniZodiacali.put(1, nomiSegni.get(1));
        } else if (grado >= 60 && grado < 90) {         // Gemelli
            segniZodiacali.put(2, nomiSegni.get(2));
        } else if (grado >= 90 && grado < 120) {        // Cancro
            segniZodiacali.put(3, nomiSegni.get(3));
        } else if (grado >= 120 && grado < 150) {       // Leone
            segniZodiacali.put(4, nomiSegni.get(4));
        } else if (grado >= 150 && grado < 180) {       // Vergine
            segniZodiacali.put(5, nomiSegni.get(5));
        } else if (grado >= 180 && grado < 210) {       // Bilancia
            segniZodiacali.put(6, nomiSegni.get(6));
        } else if (grado >= 210 && grado < 240) {       // Scorpione
            segniZodiacali.put(7, nomiSegni.get(7));
        } else if (grado >= 240 && grado < 270) {       // Sagittario
            segniZodiacali.put(8, nomiSegni.get(8));
        } else if (grado >= 270 && grado < 300) {       // Capricorno
            segniZodiacali.put(9, nomiSegni.get(9));
        } else if (grado >= 300 && grado < 330) {       // Acquario
            segniZodiacali.put(10, nomiSegni.get(10));
        } else if (grado >= 330 && grado < 360) {       // Pesci
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




}
