package com.lunasapiens;

import com.lunasapiens.dto.GiornoOraPosizioneDTO;
import com.lunasapiens.entity.OroscopoGiornaliero;
import com.lunasapiens.repository.OroscopoGiornalieroRepository;
import com.lunasapiens.service.EmailService;
import com.lunasapiens.service.OroscopoGiornalieroService;
import com.lunasapiens.zodiac.ServizioOroscopoDelGiorno;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.awt.*;
import java.text.SimpleDateFormat;

@Component
public class ScheduledTasks {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

    @Autowired
    private ServizioOroscopoDelGiorno servizioOroscopoDelGiorno;

    @Autowired
    private OroscopoGiornalieroService oroscopoGiornalieroService;

    @Autowired
    private OroscopoGiornalieroRepository oroscopoGiornalieroRepository;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private TelegramBotClient telegramBotClient;

    @Autowired
    private EmailService emailService;


    // (* secondi * minuti * ore * giorno del mese * mese * giorno della settimana)
    // settato per le 23:50 ogni giorno: "0 50 23 * * *"
    // settato per le 00:05 ogni giorno: "0 5 0 * * *"
    @Scheduled(cron = "0 3 0 * * *", zone = "Europe/Rome")
    public void executeTask_CreaOroscopoGiornaliero() {
        creaOroscopoGiornaliero();
        telegramBotClient.inviaMessaggio("executeTask Eseguito! ScheduledTasks.executeTask() "+ Util.getNowRomeEurope());
        logger.info("executeTask_CreaOroscopoGiornaliero eseguito alle " + Util.getNowRomeEurope());
    }

    @Scheduled(cron = "0 20 0 * * *", zone = "Europe/Rome")
    public void executeTask_InvioProfiloUtenteOroscopoGiornaliero() {
        emailService.inviaEmailOrosciopoGioraliero();
        logger.info("executeTask_InvioProfiloUtenteOroscopoGiornaliero eseguito alle " + Util.getNowRomeEurope());
    }


    public void test_Oroscopo_Segni_Transiti_Aspetti(){
        servizioOroscopoDelGiorno.Oroscopo_Segni_Transiti_Aspetti();
    }






    public void creaOroscopoGiornaliero() {
        String pathOroscopoGiornalieroImmagini = Constants.PATH_STATIC + GeneratorImage.folderOroscopoGiornalieroImmagine;
        GiornoOraPosizioneDTO giornoOraPosizioneDTO = Util.GiornoOraPosizione_OggiRomaOre12();

        Cache cache = cacheManager.getCache(Constants.VIDEO_CACHE);
        cache.invalidate();

        logger.info("elimino cartelle e file dal classpath...");
        // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ ELIMINO LE CARTELLE E FILE @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
        Util.eliminaCartelleEFile(pathOroscopoGiornalieroImmagini);

        for (int numeroSegno = 0; numeroSegno <= 11; numeroSegno++) {
            OroscopoGiornaliero oroscopoGiornaliero = oroscopoGiornalieroService.findByNumSegnoAndDataOroscopo(numeroSegno, Util.convertiGiornoOraPosizioneDTOInDate(giornoOraPosizioneDTO));

            if (oroscopoGiornaliero == null || oroscopoGiornaliero.getVideo() == null || oroscopoGiornaliero.getNomeFileVideo() == null
                    || oroscopoGiornaliero.getTestoOroscopo() == null) {

                if(oroscopoGiornaliero == null) {
                    oroscopoGiornaliero = new OroscopoGiornaliero();
                    oroscopoGiornaliero.setNumSegno(numeroSegno);
                    oroscopoGiornaliero.setDataOroscopo( Util.convertiGiornoOraPosizioneDTOInDate(giornoOraPosizioneDTO) );
                }
                try {
                    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ CREAZIONE CONTENUTO IA @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                    StringBuilder sBTestoOroscopoIA = null;
                    if(oroscopoGiornaliero.getTestoOroscopo() == null || oroscopoGiornaliero.getTestoOroscopo().isEmpty()) {
                        sBTestoOroscopoIA = servizioOroscopoDelGiorno.oroscopoDelGiornoIA( numeroSegno, giornoOraPosizioneDTO);
                        if(sBTestoOroscopoIA == null || sBTestoOroscopoIA.length() == 0) {
                            logger.error("sBTestoOroscopo null: Risposta nulla dalla IA. Salto iterazione del ciclo della creazione del video");
                            // l'istruzione continue viene eseguita, facendo saltare l'iterazione corrente e passando direttamente alla successiva.
                            continue;
                        }
                    } else {
                        sBTestoOroscopoIA = new StringBuilder( oroscopoGiornaliero.getTestoOroscopo() );
                    }

                    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ LAVORAZIONE TESTO @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                    ArrayList<String> paragrafiTestoOroscopoIA = ServizioOroscopoDelGiorno.dividiParagrafiStringBuilderIA( sBTestoOroscopoIA );


                    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ CREAZIONE IMMAGINE @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                    String fontName = "Comic Sans MS"; // Arial
                    int fontSize = 25; Color textColor = Color.BLUE;

                    // Formattatore per la data
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    String dataOroscopoString = formatter.format( Util.convertiGiornoOraPosizioneDTOInDate(giornoOraPosizioneDTO) );

                    String imagePath = pathOroscopoGiornalieroImmagini + dataOroscopoString + "/" + numeroSegno + "/";
                    GeneratorImage igenerat = new GeneratorImage();
                    // Itera su ogni pezzo della stringa
                    for (int i = 0; i < paragrafiTestoOroscopoIA.size(); i++) {
                        String fileName = i + ".png";
                        String imagePathFileName = imagePath + fileName;
                        igenerat.generateImage(paragrafiTestoOroscopoIA.get(i), fontName, fontSize, textColor, imagePathFileName);
                    }

                    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ CREAZIONE VIDEO @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                    String nomeFileVideo = dataOroscopoString + "_" + numeroSegno;
                    byte[] videoBytes = GeneratorVideo.createVideoFromImages(imagePath, nomeFileVideo );

                    try{
                        oroscopoGiornaliero = oroscopoGiornalieroService.salvaOroscoopoGiornaliero(numeroSegno, sBTestoOroscopoIA, giornoOraPosizioneDTO,
                                videoBytes, nomeFileVideo + GeneratorVideo.formatoVideo());

                    } catch (DataIntegrityViolationException e) {
                        oroscopoGiornaliero = oroscopoGiornalieroService.findByNumSegnoAndDataOroscopo(numeroSegno, Util.convertiGiornoOraPosizioneDTOInDate(giornoOraPosizioneDTO));
                        oroscopoGiornaliero.setNumSegno(numeroSegno);
                        oroscopoGiornaliero.setTestoOroscopo(sBTestoOroscopoIA.toString());
                        oroscopoGiornaliero.setDataOroscopo( Util.convertiGiornoOraPosizioneDTOInDate(giornoOraPosizioneDTO) );
                        oroscopoGiornaliero.setVideo(videoBytes);
                        oroscopoGiornaliero.setNomeFileVideo(nomeFileVideo + GeneratorVideo.formatoVideo());
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
        Util.eliminaCartelleEFile(pathOroscopoGiornalieroImmagini);

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







}
