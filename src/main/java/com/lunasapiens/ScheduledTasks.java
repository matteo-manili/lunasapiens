package com.lunasapiens;

import com.lunasapiens.dto.GiornoOraPosizioneDTO;
import com.lunasapiens.entity.OroscopoGiornaliero;
import com.lunasapiens.repository.OroscopoGiornalieroRepository;
import com.lunasapiens.service.OroscopoGiornalieroService;
import com.lunasapiens.zodiac.ServiziAstrologici;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;

import java.awt.*;
import java.io.File;

import java.text.SimpleDateFormat;
import java.util.List;

@Component
public class ScheduledTasks {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

    @Autowired
    private OroscopoGiornalieroService oroscopoGiornalieroService;

    @Autowired
    private OroscopoGiornalieroRepository oroscopoGiornalieroRepository;

    @Autowired
    private AppConfig appConfig;


    @Scheduled(cron = "0 0 12 * * *", zone = "Europe/Rome")
    public void executeTask() {
        // Implementa qui il codice da eseguire
        System.out.println("Task eseguito alle " + LocalDateTime.now());
    }


    public void creaOroscopoGiornaliero() {

        // ciclo i 12 segni astrologici
        for (int numeroSegno = 1; numeroSegno <= 3; numeroSegno++) {


            if (oroscopoGiornalieroService.existsByNumSegnoAndDataOroscopo(numeroSegno, Util.OggiRomaOre12()) == false) {
                logger.info("Il record NON esiste");

                // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ CREAZIONE CONTENUTO IA @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                ServiziAstrologici sA = new ServiziAstrologici(appConfig.getKeyOpenAi());
                GiornoOraPosizioneDTO giornoOraPosizioneDTO = Util.GiornoOraPosizione_OggiRomaOre12();

                OroscopoGiornaliero aaa = oroscopoGiornalieroService.findByNumSegnoAndDataOroscopo(numeroSegno, Util.convertiGiornoOraPosizioneDTOInDate(giornoOraPosizioneDTO));

                StringBuilder sB;

                if(aaa == null || aaa.getTestoOroscopo() == null){
                    sB = sA.servizioOroscopoDelGiorno(Constants.segniZodiacali().get(numeroSegno), giornoOraPosizioneDTO);
                }else{
                    sB = new StringBuilder( aaa.getTestoOroscopo() );
                }


                try{

                    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ LAVORAZIONE TESTO @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                    ArrayList<String> pezziStringa = estraiPezziStringa( sB.toString() );


                    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ CREAZIONE IMMAGINE @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                    String fontName = "Arial"; int fontSize = 20; Color textColor = Color.BLUE;
                    // Formattatore per la data
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    // Converti la data nel formato desiderato
                    String dataOroscopoString = formatter.format( Util.convertiGiornoOraPosizioneDTOInDate(giornoOraPosizioneDTO) );

                    String inputImagePath = "src/main/resources/static/oroscopo_giornaliero/immagini/" + dataOroscopoString + "/" + numeroSegno + "/";

                    ImageGenerator igenerat = new ImageGenerator();
                    // Itera su ogni pezzo della stringa
                    for (int i = 0; i < pezziStringa.size(); i++) {
                        // Genera il nome del file basato sul numero del ciclo e sull'id del codice Oroscopo
                        String fileName = i + ".png";
                        // Imposta il percorso di output per salvare l'immagine nella cartella specificata
                        String outputPath = inputImagePath + fileName;
                        // Genera l'immagine per il pezzo corrente con il font specificato
                        igenerat.generateImage(pezziStringa.get(i), fontName, fontSize, textColor, outputPath);
                    }

                    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ CREAZIONE VIDEO @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                    try{
                        String nomeFileVideo = dataOroscopoString + "_" + numeroSegno;
                        byte[] videoBytes = VideoGenerator.createVideoFromImages(inputImagePath, nomeFileVideo );

                        OroscopoGiornaliero oroscopoGiornaliero = new OroscopoGiornaliero();
                        oroscopoGiornaliero.setVideo(videoBytes);
                        oroscopoGiornaliero.setNomeFileVideo( nomeFileVideo + VideoGenerator.formatoVideo());

                        try{
                            oroscopoGiornaliero = oroscopoGiornalieroService.salvaOroscoopoGiornaliero(numeroSegno, sB, giornoOraPosizioneDTO, null, null);

                        } catch (DataIntegrityViolationException e) {
                            oroscopoGiornaliero = oroscopoGiornalieroService.findByNumSegnoAndDataOroscopo(numeroSegno, Util.convertiGiornoOraPosizioneDTOInDate(giornoOraPosizioneDTO));
                            oroscopoGiornaliero.setNumSegno(numeroSegno);
                            oroscopoGiornaliero.setTestoOroscopo(sB.toString());
                            oroscopoGiornaliero.setDataOroscopo( Util.convertiGiornoOraPosizioneDTOInDate(giornoOraPosizioneDTO) );
                            oroscopoGiornaliero.setVideo(videoBytes);
                            oroscopoGiornaliero.setNomeFileVideo( nomeFileVideo + VideoGenerator.formatoVideo());
                            oroscopoGiornalieroRepository.save(oroscopoGiornaliero);
                        }


                        // @@@@@@@@@@ SALVA VIDEO SU CARTELLA STATIC @@@@@@@@@@@@@@@@@@@






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

                //List<OroscopoGiornaliero> videos = oroscopoGiornalieroService.findAllByDataOroscopo(Util.OggiRomaOre12());

                // TODO continuare.............................
                // salvare i video dal database nelle cartelle src/... video.... Così sono più reperibili velocemente.




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




