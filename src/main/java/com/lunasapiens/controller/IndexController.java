package com.lunasapiens.controller;


import com.lunasapiens.AppConfig;
import com.lunasapiens.Constants;
import com.lunasapiens.ImageGenerator;
import com.lunasapiens.VideoGenerator;
import com.lunasapiens.dto.GiornoOraPosizioneDTO;
import com.lunasapiens.entity.OroscopoGiornaliero;
import com.lunasapiens.service.OroscopoGiornalieroService;
import com.lunasapiens.zodiac.ServiziAstrologici;
import com.lunasapiens.zodiac.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;


import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;

@Controller
public class IndexController {

    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private AppConfig appConfig;

    private OroscopoGiornalieroService oroscopoGiornalieroService;

    @Autowired
    public IndexController(OroscopoGiornalieroService oroscopoGiornalieroService) {
        this.oroscopoGiornalieroService = oroscopoGiornalieroService;
    }

    @GetMapping("/")
    public String index(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("message", "Welcome to our dynamic landing page!");
        return "index";
    }


    @GetMapping("/info-privacy")
    public String infoPrivacy(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("name", name);
        return "info-privacy";
    }

    @GetMapping("/termini-di-servizio")
    public String terminiDiServizio(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("name", name);
        return "termini-di-servizio";
    }



    @GetMapping("/oroscopo")
    public String oroscopo(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {

        //09-03-1996 ore 21
        //Latitudine di Palermo	38.1156879
        //Longitudine di Palermo	13.3612671

        int ora = 21; int minuti = 0;
        int giorno = 9; int mese = 3; int anno = 1996;
        double lon = 38.1; double lat = 13.3;

        /*
        int ora = 12; int minuti = 0;
        int giorno = 30; int mese = 9; int anno = 2023;
        double lon = 49.9; double lat = 12.4;
         */

        int segnoNumero = 10;
        ServiziAstrologici sA = new ServiziAstrologici(appConfig.getKeyOpenAi());
        GiornoOraPosizioneDTO giornoOraPosizioneDTO = Util.GiornoOraPosizione_OggiRomaOre12();

        //StringBuilder sB = sA.servizioOroscopoDelGiorno(Constants.segniZodiacali().get( segnoNumero ), giornoOraPosizioneDTO);
        //OroscopoGiornaliero oroscopoGiornaliero = oroscopoGiornalieroService.salvaOroscoopoGiornaliero(segnoNumero, sB, giornoOraPosizioneDTO);



        // @@@@@@@ò@ crezione immagine @@@@@@@@
        String text = "Hello, World!";
        String fontName = "Arial";
        int fontSize = 20;
        Color textColor = Color.BLUE;

        ImageGenerator igenerat = new ImageGenerator();
        //igenerat.generateImage(text, fontName, fontSize, textColor, "src/main/resources/static/image-generated/generatedImage.png");

        // Verifica se il file esiste
        File imageFileExists = new File("src/main/resources/static/image-generated/generatedImage.png");
        boolean imageExists = imageFileExists.exists();

        if (imageExists) {
            // L'immagine è stata generata con successo
            logger.info("L'immagine è stata generata con successo");
            model.addAttribute("imageExists", true);
        } else {
            // L'immagine non è stata generata
            logger.info("L'immagine non è stata generata");
            model.addAttribute("imageExists", false);
        }

        // @@@@@@@@@@@ creazione video @@@@@@@@@


        try{
            VideoGenerator aa = new VideoGenerator();
            aa.createVideoFromImages();

        }catch(Exception exc){
            exc.printStackTrace();
        }




        // @@@@@@ fine creazione video
        model.addAttribute("oroscopoGpt", ""/*oroscopoGiornaliero.getTestoOroscopo()*/ );
        return "oroscopo";
    }





}
