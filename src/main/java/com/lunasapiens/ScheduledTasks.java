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
import org.springframework.core.io.ByteArrayResource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

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


    @Scheduled(cron = "0 0 6 * * *", zone = "Europe/Rome")
    public void executeTask() {
        creaOroscopoGiornaliero();
        telegramBotClient.inviaMessaggio("Eseguito! ScheduledTasks.executeTask() "+ LocalDateTime.now());
        logger.info("Task eseguito alle " + LocalDateTime.now());
    }


    public void creaOroscopoGiornaliero() {
        // ciclo i 12 segni astrologici
        for (int numeroSegno = 1; numeroSegno <= 12; numeroSegno++) {

            GiornoOraPosizioneDTO giornoOraPosizioneDTO = Util.GiornoOraPosizione_OggiRomaOre12();
            OroscopoGiornaliero oroscopoGiornaliero = oroscopoGiornalieroService.findByNumSegnoAndDataOroscopo(numeroSegno, Util.convertiGiornoOraPosizioneDTOInDate(giornoOraPosizioneDTO));

            logger.info( "Util.convertiGiornoOraPosizioneDTOInDate(giornoOraPosizioneDTO): "
                    + Util.convertiGiornoOraPosizioneDTOInDate(giornoOraPosizioneDTO).toString() );

            if (oroscopoGiornaliero == null || oroscopoGiornaliero.getVideo() == null || oroscopoGiornaliero.getNomeFileVideo() == null
                    || oroscopoGiornaliero.getTestoOroscopo() == null) {

                if(oroscopoGiornaliero == null){
                    oroscopoGiornaliero = new OroscopoGiornaliero();
                    oroscopoGiornaliero.setNumSegno(numeroSegno);
                    oroscopoGiornaliero.setDataOroscopo( Util.convertiGiornoOraPosizioneDTOInDate(giornoOraPosizioneDTO) );
                }

                // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ CREAZIONE CONTENUTO IA @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                StringBuilder sB;
                if(oroscopoGiornaliero.getTestoOroscopo() == null || oroscopoGiornaliero.getTestoOroscopo().isEmpty() ){
                    ServiziAstrologici sA = new ServiziAstrologici(appConfig.getKeyOpenAi());
                    sB = sA.oroscopoDelGiornoIA(Constants.segniZodiacali().get(numeroSegno -1), giornoOraPosizioneDTO);
                    oroscopoGiornaliero.setTestoOroscopo(sB.toString());
                }else{
                    sB = new StringBuilder( oroscopoGiornaliero.getTestoOroscopo() );
                }

                try{
                    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ LAVORAZIONE TESTO @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                    ArrayList<String> pezziStringa = estraiPezziStringa( sB.toString() );

                    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ CREAZIONE IMMAGINE @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                    String fontName = "Comic Sans MS"; // Arial
                    int fontSize = 40; Color textColor = Color.BLUE;

                    // Formattatore per la data
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    String dataOroscopoString = formatter.format( Util.convertiGiornoOraPosizioneDTOInDate(giornoOraPosizioneDTO) );

                    String pathOroscopoGiornalieroImmagini = Constants.PATH_STATIC + "oroscopo_giornaliero/immagini/";
                    String inputImagePath = pathOroscopoGiornalieroImmagini + dataOroscopoString + "/" + numeroSegno + "/";
                    ImageGenerator igenerat = new ImageGenerator();
                    // Itera su ogni pezzo della stringa
                    for (int i = 0; i < pezziStringa.size(); i++) {
                        String fileName = i + ".png";
                        String outputPath = inputImagePath + fileName;
                        igenerat.generateImage(pezziStringa.get(i), fontName, fontSize, textColor, outputPath);
                    }

                    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ CREAZIONE VIDEO @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                    try{
                        String nomeFileVideo = dataOroscopoString + "_" + numeroSegno;
                        byte[] videoBytes = VideoGenerator.createVideoFromImages(inputImagePath, nomeFileVideo );

                        try{
                            oroscopoGiornaliero = oroscopoGiornalieroService.salvaOroscoopoGiornaliero(numeroSegno, sB, giornoOraPosizioneDTO,
                                    videoBytes, nomeFileVideo + VideoGenerator.formatoVideo());

                        } catch (DataIntegrityViolationException e) {
                            oroscopoGiornaliero = oroscopoGiornalieroService.findByNumSegnoAndDataOroscopo(numeroSegno, Util.convertiGiornoOraPosizioneDTOInDate(giornoOraPosizioneDTO));
                            oroscopoGiornaliero.setNumSegno(numeroSegno);
                            oroscopoGiornaliero.setTestoOroscopo(sB.toString());
                            oroscopoGiornaliero.setDataOroscopo( Util.convertiGiornoOraPosizioneDTOInDate(giornoOraPosizioneDTO) );
                            oroscopoGiornaliero.setVideo(videoBytes);
                            oroscopoGiornaliero.setNomeFileVideo(nomeFileVideo + VideoGenerator.formatoVideo());
                            oroscopoGiornalieroRepository.save(oroscopoGiornaliero);
                        }

                        // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ SALVA VIDEO SU NELLA CACHE @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                        Cache cache = cacheManager.getCache(Constants.VIDEO_CACHE);
                        cache.put(nomeFileVideo+VideoGenerator.formatoVideo(), Util.VideoResponseEntityByteArrayResource(videoBytes));


                        // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ ELIMINO LE CARTELLE E FILE @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                        File directoryImmagini = new File(pathOroscopoGiornalieroImmagini);
                        Util.deleteDirectory(directoryImmagini);
                        File directoryVideo = new File(VideoGenerator.pathOroscopoGiornalieroVideo);
                        Util.deleteDirectory(directoryVideo);

                    }catch(Exception exc){
                        exc.printStackTrace();
                    }

                } catch (DataIntegrityViolationException e) {
                    logger.info("Error DataIntegrityViolationException in the database: " + e.getMessage());
                    e.printStackTrace();
                } catch (Exception e) {
                    logger.info("Error updating value in the database: " + e.getMessage());
                    e.printStackTrace();
                }

            } else {
                logger.info("Il record esiste");
                Cache cache = cacheManager.getCache(Constants.VIDEO_CACHE);
                cache.put(oroscopoGiornaliero.getNomeFileVideo(), Util.VideoResponseEntityByteArrayResource(oroscopoGiornaliero.getVideo()));

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




}




