package com.lunasapiens.controller;


import com.lunasapiens.*;
import com.lunasapiens.entity.OroscopoGiornaliero;
import com.lunasapiens.service.OroscopoGiornalieroService;
import com.lunasapiens.service.VideoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class IndexController {

    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private ScheduledTasks scheduledTasks;

    private OroscopoGiornalieroService oroscopoGiornalieroService;

    private ResourceLoader resourceLoader;

    private final VideoService videoService;

    @Autowired
    public IndexController(OroscopoGiornalieroService oroscopoGiornalieroService, VideoService videoService) {
        this.oroscopoGiornalieroService = oroscopoGiornalieroService;
        this.resourceLoader = resourceLoader;
        this.videoService = videoService;
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
    public String mostraOroscopo(Model model) {

        scheduledTasks.creaOroscopoGiornaliero();


        List<OroscopoGiornaliero> listOroscopoGiorn = oroscopoGiornalieroService.findAllByDataOroscopoWithoutVideo( Util.OggiRomaOre12() );

        model.addAttribute("videos", listOroscopoGiorn);
        return "oroscopo";
    }



    @GetMapping("/oroscopo-giornaliero/{videoName}")
    @ResponseBody
    public ResponseEntity<byte[]> getVideo(@PathVariable String videoName) {

        logger.info("sono in oroscopo-giornaliero videoName: "+videoName);
        
        if (!videoService.videoExists( videoName )) {

            logger.info("il video non c'è su videoService");

            OroscopoGiornaliero oroscopoGiornaliero = oroscopoGiornalieroService.findByNomeFileVideo(videoName);
            byte[] videoBytes = oroscopoGiornaliero.getVideo();

            videoService.saveVideo( oroscopoGiornaliero.getNomeFileVideo(), videoBytes);

        }

        // Recupera il video dal VideoService utilizzando il nome univoco
        byte[] videoBytes = videoService.getVideoByName(videoName);

        // Se il video è stato trovato, restituisci una risposta con il video
        if (videoBytes != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", videoName);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(videoBytes);
        } else {
            // Se il video non è stato trovato, restituisci una risposta 404 Not Found
            return ResponseEntity.notFound().build();
        }
    }




}
