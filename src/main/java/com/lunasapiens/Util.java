package com.lunasapiens;

import com.lunasapiens.dto.GiornoOraPosizioneDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;

public class Util {

    private static final Logger logger = LoggerFactory.getLogger(Util.class);


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



    public static String determinaSegnoZodiacale(double grado) {
        if (grado >= 0 && grado < 31) {
            return Constants.segniZodiacali().get(0);
        } else if (grado >= 31 && grado < 61) {
            return Constants.segniZodiacali().get(1);
        } else if (grado >= 61 && grado < 91) {
            return Constants.segniZodiacali().get(2);
        } else if (grado >= 91 && grado < 121) {
            return Constants.segniZodiacali().get(3);
        } else if (grado >= 121 && grado < 151) {
            return Constants.segniZodiacali().get(4);
        } else if (grado >= 151 && grado < 181) {
            return Constants.segniZodiacali().get(5);
        } else if (grado >= 181 && grado < 211) {
            return Constants.segniZodiacali().get(6);
        } else if (grado >= 211 && grado < 241) {
            return Constants.segniZodiacali().get(7);
        } else if (grado >= 241 && grado < 271) {
            return Constants.segniZodiacali().get(8);
        } else if (grado >= 271 && grado < 301) {
            return Constants.segniZodiacali().get(9);
        } else if (grado >= 301 && grado < 331) {
            return Constants.segniZodiacali().get(10);
        } else if (grado >= 331 && grado < 361) {
            return Constants.segniZodiacali().get(11);
        } else {
            return "Grado non valido";
        }
    }

}
