package com.lunasapiens;

import com.lunasapiens.dto.GiornoOraPosizioneDTO;
import com.lunasapiens.entity.OroscopoGiornaliero;
import com.lunasapiens.repository.OroscopoGiornalieroRepository;
import com.lunasapiens.service.OroscopoGiornalieroService;
import com.lunasapiens.zodiac.ServiziAstrologici;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.awt.*;
import java.text.SimpleDateFormat;

@Component
public class ScheduledTasks {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

    @Autowired
    private OroscopoGiornalieroService oroscopoGiornalieroService;

    @Autowired
    private OroscopoGiornalieroRepository oroscopoGiornalieroRepository;

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private TelegramBotClient telegramBotClient;

    // (* secondi * minuti * ore * giorno del mese * mese * giorno della settimana)
    // settato per le 23:50 ogni giorno: "0 50 23 * * *"
    // settato per le 00:05 ogni giorno: "0 5 0 * * *"
    @Scheduled(cron = "0 3 0 * * *", zone = "Europe/Rome")
    public void executeTask() {
        creaOroscopoGiornaliero();
        telegramBotClient.inviaMessaggio("executeTask Eseguito! ScheduledTasks.executeTask() "+ Util.getNowRomeEurope());
        logger.info("executeTask eseguito alle " + Util.getNowRomeEurope());
    }

    // eseguo 2 volte il task, perché lascia sempre qualche video a null
    //@Scheduled(cron = "0 6 0 * * *", zone = "Europe/Rome")
    public void executeTask_2() {
        creaOroscopoGiornaliero();
        telegramBotClient.inviaMessaggio("executeTask_2 Eseguito! ScheduledTasks.executeTask() "+ Util.getNowRomeEurope());
        logger.info("executeTask_2 eseguito alle " + Util.getNowRomeEurope());
    }


    public void creaOroscopoGiornaliero() {
        String pathOroscopoGiornalieroImmagini = Constants.PATH_STATIC + "oroscopo_giornaliero/immagini/";
        GiornoOraPosizioneDTO giornoOraPosizioneDTO = Util.GiornoOraPosizione_OggiRomaOre12();

        Cache cache = cacheManager.getCache(Constants.VIDEO_CACHE);
        cache.invalidate();

        logger.info("elimino cartelle e file dal classpath...");
        // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ ELIMINO LE CARTELLE E FILE @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
        eliminaCartelleEFile(pathOroscopoGiornalieroImmagini);

        for (int numeroSegno = 1; numeroSegno <= 12; numeroSegno++) {
            OroscopoGiornaliero oroscopoGiornaliero = oroscopoGiornalieroService.findByNumSegnoAndDataOroscopo(numeroSegno, Util.convertiGiornoOraPosizioneDTOInDate(giornoOraPosizioneDTO));

            if (oroscopoGiornaliero == null || oroscopoGiornaliero.getVideo() == null || oroscopoGiornaliero.getNomeFileVideo() == null
                    || oroscopoGiornaliero.getTestoOroscopo() == null) {

                if(oroscopoGiornaliero == null){
                    oroscopoGiornaliero = new OroscopoGiornaliero();
                    oroscopoGiornaliero.setNumSegno(numeroSegno);
                    oroscopoGiornaliero.setDataOroscopo( Util.convertiGiornoOraPosizioneDTOInDate(giornoOraPosizioneDTO) );
                }
                try{
                    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ CREAZIONE CONTENUTO IA @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                    StringBuilder sBTestoOroscopoIA = null;
                    if(oroscopoGiornaliero.getTestoOroscopo() == null || oroscopoGiornaliero.getTestoOroscopo().isEmpty() ){
                        boolean found = false; int tentativi = 0;
                        while (!found && tentativi < 3) {
                            ServiziAstrologici sA = new ServiziAstrologici(appConfig.getKeyOpenAi());
                            sBTestoOroscopoIA = sA.oroscopoDelGiornoIA(Constants.segniZodiacali().get(numeroSegno -1), giornoOraPosizioneDTO);
                            if (sBTestoOroscopoIA.toString().contains(Constants.SeparatoreTestoOroscopo)) {
                                logger.info("La stringa "+Constants.SeparatoreTestoOroscopo.toString()+" è stata trovata nel StringBuilder.");
                                found = true;
                            } else {
                                logger.info("La stringa "+Constants.SeparatoreTestoOroscopo.toString()+" non è stata trovata nel StringBuilder.");
                                tentativi++;
                            }
                        }
                        if (!found) {
                            logger.info("Limite di tentativi raggiunto. La stringa "+Constants.SeparatoreTestoOroscopo.toString()+" non è stata trovata.");
                            logger.info("sBTestoOroscopo null: esco dal ciclo generale della ctrazione del video");
                            break;
                        }
                    }else{
                        sBTestoOroscopoIA = new StringBuilder( oroscopoGiornaliero.getTestoOroscopo() );
                    }



                    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ LAVORAZIONE TESTO @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                    ArrayList<String> pezziStringa = estraiPezziStringa( sBTestoOroscopoIA.toString() );

                    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ CREAZIONE IMMAGINE @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                    String fontName = "Comic Sans MS"; // Arial
                    int fontSize = 25; Color textColor = Color.BLUE;

                    // Formattatore per la data
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    String dataOroscopoString = formatter.format( Util.convertiGiornoOraPosizioneDTOInDate(giornoOraPosizioneDTO) );

                    String imagePath = pathOroscopoGiornalieroImmagini + dataOroscopoString + "/" + numeroSegno + "/";
                    ImageGenerator igenerat = new ImageGenerator();
                    // Itera su ogni pezzo della stringa
                    for (int i = 0; i < pezziStringa.size(); i++) {
                        String fileName = i + ".png";
                        String imagePathFileName = imagePath + fileName;
                        igenerat.generateImage(pezziStringa.get(i), fontName, fontSize, textColor, imagePathFileName);
                    }

                    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ CREAZIONE VIDEO @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                    String nomeFileVideo = dataOroscopoString + "_" + numeroSegno;
                    byte[] videoBytes = VideoGenerator.createVideoFromImages(imagePath, nomeFileVideo );

                    try{
                        oroscopoGiornaliero = oroscopoGiornalieroService.salvaOroscoopoGiornaliero(numeroSegno, sBTestoOroscopoIA, giornoOraPosizioneDTO,
                                videoBytes, nomeFileVideo + VideoGenerator.formatoVideo());

                    } catch (DataIntegrityViolationException e) {
                        oroscopoGiornaliero = oroscopoGiornalieroService.findByNumSegnoAndDataOroscopo(numeroSegno, Util.convertiGiornoOraPosizioneDTOInDate(giornoOraPosizioneDTO));
                        oroscopoGiornaliero.setNumSegno(numeroSegno);
                        oroscopoGiornaliero.setTestoOroscopo(sBTestoOroscopoIA.toString());
                        oroscopoGiornaliero.setDataOroscopo( Util.convertiGiornoOraPosizioneDTOInDate(giornoOraPosizioneDTO) );
                        oroscopoGiornaliero.setVideo(videoBytes);
                        oroscopoGiornaliero.setNomeFileVideo(nomeFileVideo + VideoGenerator.formatoVideo());
                        oroscopoGiornalieroRepository.save(oroscopoGiornaliero);
                    }
                } catch (Exception e) {
                    logger.info("Error in creaOroscopoGiornaliero(): " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        logger.info("faccio pausa Thread.sleep......");
        try{
            // Faccio una pausa per smalite le operazioni fatte
            Thread.sleep(10000); // 10000 = 10 secondi
        }catch(InterruptedException e){
            e.printStackTrace();
        }

        logger.info("elimino cartelle e file dal classpath...");
        // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ ELIMINO LE CARTELLE E FILE @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
        eliminaCartelleEFile(pathOroscopoGiornalieroImmagini);

        logger.info("metto i video in cache...");
        // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ SALVA VIDEO SU NELLA CACHE @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
        java.util.List<OroscopoGiornaliero> listOroscopoGiorn = oroscopoGiornalieroService.findAllByDataOroscopo(Util.OggiOre12());
        for (OroscopoGiornaliero oroscopoGiorno : listOroscopoGiorn) {
            if(oroscopoGiorno.getVideo() != null){
                cache.put(oroscopoGiorno.getNomeFileVideo(), Util.VideoResponseEntityByteArrayResource(oroscopoGiorno.getVideo()));
            }
        }
        logger.info("Fine Task creaOroscopoGiornaliero.");
    }


    private void eliminaCartelleEFile(String pathOroscopoGiornalieroImmagini) {
        File directoryImmagini = new File(pathOroscopoGiornalieroImmagini);
        Util.deleteDirectory(directoryImmagini);
        File directoryVideo = new File(VideoGenerator.pathOroscopoGiornalieroVideo);
        Util.deleteDirectory(directoryVideo);
    }

    private ArrayList<String> estraiPezziStringa(String testoCompleto) {
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




}
