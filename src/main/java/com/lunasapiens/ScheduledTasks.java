package com.lunasapiens;

import com.lunasapiens.controller.IndexController;
import com.lunasapiens.dto.GiornoOraPosizioneDTO;
import com.lunasapiens.entity.OroscopoGiornaliero;
import com.lunasapiens.service.OroscopoGiornalieroService;
import com.lunasapiens.zodiac.ServiziAstrologici;
import com.lunasapiens.zodiac.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class ScheduledTasks {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

    @Autowired
    private OroscopoGiornalieroService oroscopoGiornalieroService;

    @Autowired
    private AppConfig appConfig;


    @Scheduled(cron = "0 0 12 * * *", zone = "Europe/Rome")
    public void executeTask() {
        // Implementa qui il codice da eseguire
        System.out.println("Task eseguito alle " + LocalDateTime.now());
    }


    public void creaOroscopoGiornaliero() {

        // ciclo i 12 segni astrologici
        for (int numeroSegno = 1; numeroSegno <= 2; numeroSegno++) {

            if (oroscopoGiornalieroService.existsByNumSegnoAndDataOroscopo(numeroSegno, Util.OggiRomaOre12()) == false) {
                logger.info("Il record NON esiste");

                // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ CREAZIONE CONTENUTO IA @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                ServiziAstrologici sA = new ServiziAstrologici(appConfig.getKeyOpenAi());
                GiornoOraPosizioneDTO giornoOraPosizioneDTO = Util.GiornoOraPosizione_OggiRomaOre12();

                StringBuilder sB = sA.servizioOroscopoDelGiorno(Constants.segniZodiacali().get(numeroSegno), giornoOraPosizioneDTO);

                if(oroscopoGiornalieroService.existsByNumSegnoAndDataOroscopo(numeroSegno, Util.OggiRomaOre12()) == false) {
                    oroscopoGiornalieroService.salvaOroscoopoGiornaliero(numeroSegno, sB, giornoOraPosizioneDTO);
                }

                // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ LAVORAZIONE TESTO @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                OroscopoGiornaliero oroscopoGiornaliero = oroscopoGiornalieroService.findByNumSegnoAndDataOroscopo(i, Util.OggiRomaOre12());
                ArrayList<String> pezziStringa = estraiPezziStringa(oroscopoGiornaliero.getTestoOroscopo());


                // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ CREAZIONE IMMAGINE @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                String fontName = "Arial"; int fontSize = 20; Color textColor = Color.BLUE;
                // Formattatore per la data
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                // Converti la data nel formato desiderato
                String dataOroscopoString = formatter.format(oroscopoGiornaliero.getDataOroscopo());


                String directoryPath = "src/main/resources/static/oroscopo_giornaliero/immagini/" + dataOroscopoString + "/" + numeroSegno + "/";

                // Elimina la cartella se esiste
                File directory = new File(directoryPath);
                if (directory.exists()) {
                    deleteDirectory(directory);
                }
                directory.mkdirs();

                ImageGenerator igenerat = new ImageGenerator();

                // Itera su ogni pezzo della stringa
                for (int i = 0; i < pezziStringa.size(); i++) {
                    // Genera il nome del file basato sul numero del ciclo e sull'id del codice Oroscopo
                    String fileName = i + ".png";

                    // Imposta il percorso di output per salvare l'immagine nella cartella specificata
                    String outputPath = directoryPath + fileName;

                    // Genera l'immagine per il pezzo corrente con il font specificato
                    igenerat.generateImage(pezziStringa.get(i), fontName, fontSize, textColor, outputPath);
                }

                // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ CREAZIONE VIDEO @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                try{
                    VideoGenerator aa = new VideoGenerator();
                    String nomeFileVideo = dataOroscopoString + "_" + numeroSegno;
                    aa.createVideoFromImages(directoryPath, nomeFileVideo);

                }catch(Exception exc){
                    exc.printStackTrace();
                }


            } else {
                logger.info("Il record esiste");

            }


        }
    }




    public ArrayList<String> estraiPezziStringa(String testoCompleto) {
        ArrayList<String> pezziStringa = new ArrayList<>();
        int startIndex = 0;
        int endIndex;

        while ((endIndex = testoCompleto.indexOf(Constants.SeparatoreTestoOroscopo, startIndex)) != -1) {
            String pezzo = testoCompleto.substring(startIndex, endIndex);
            pezziStringa.add(pezzo);
            startIndex = endIndex + 3; // Avanza oltre il carattere speciale "#@#"
        }

        return pezziStringa;
    }


    private void deleteDirectory(File directory) {
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

}




