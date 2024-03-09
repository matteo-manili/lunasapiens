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


    public static ZonedDateTime getNowRomeEurope(){
        ZoneId romaZone = ZoneId.of("Europe/Rome");
        ZonedDateTime now = ZonedDateTime.now(romaZone);
        logger.info("ZonedDateTime Roma_:" +now);
        return now;
    }


    public static Date OggiOre12(){
        ZonedDateTime now = getNowRomeEurope();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, now.getDayOfMonth());
        calendar.set(Calendar.MONTH, now.getMonthValue()-1);
        calendar.set(Calendar.YEAR, now.getYear());
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        // Ottenere l'oggetto Date dal Calendar

        return calendar.getTime();
    }

    /**
     * Roma 49.9 e 12.4 --- Pisa 43.7 e 10.4
     */
    public static GiornoOraPosizioneDTO GiornoOraPosizione_OggiRomaOre12(){
        ZonedDateTime now = getNowRomeEurope();
        GiornoOraPosizioneDTO giornoOraPosizioneDTO = new GiornoOraPosizioneDTO(12, 0, now.getDayOfMonth(),
                now.getMonthValue(), now.getYear(), 49.9, 12.4);
        return giornoOraPosizioneDTO;
    }


    public static Date convertiGiornoOraPosizioneDTOInDate(GiornoOraPosizioneDTO giornoOraPosizioneDTO){
        // Creare un oggetto Calendar e impostare i valori
        Calendar calendar = Calendar.getInstance();
        calendar.set(giornoOraPosizioneDTO.getAnno(), giornoOraPosizioneDTO.getMese()-1, giornoOraPosizioneDTO.getGiorno(), giornoOraPosizioneDTO.getOra(),
                giornoOraPosizioneDTO.getMinuti()); // I secondi sono impostati a 0
        // Impostare i millisecondi e secondi a 0
        calendar.set(Calendar.SECOND, 0); calendar.set(Calendar.MILLISECOND, 0);
        // Ottenere l'oggetto Date dal Calendar
        return calendar.getTime();
    }


    public static void createDirectory(String pathDirectory){
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


    public static ResponseEntity<ByteArrayResource> VideoResponseEntityByteArrayResource(byte[] videoBytes){
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
