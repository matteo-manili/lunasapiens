package com.lunasapiens.controller;


import com.lunasapiens.*;
import com.lunasapiens.entity.OroscopoGiornaliero;
import com.lunasapiens.service.OroscopoGiornalieroService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.util.*;

import java.util.List;

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

    @Autowired
    private ScheduledTasks scheduledTasks;

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



    /*
    @GetMapping("/oroscopo")
    public String oroscopo(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {

        scheduledTasks.creaOroscopoGiornaliero();


        model.addAttribute("oroscopoGpt", "" );
        return "oroscopo";
    }
    */



    @GetMapping("/oroscopo")
    public String mostraOroscopo(Model model) {

        scheduledTasks.creaOroscopoGiornaliero();

        /*
        File directory = new File("src/main/resources/static/oroscopo_giornaliero/video");
        File[] files = directory.listFiles();
        List<Map<String, String>> videos = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    Map<String, String> video = new HashMap<>();
                    video.put("name", file.getName());
                    video.put("path", "/oroscopo_giornaliero/video/" + file.getName());
                    videos.add(video);
                }
            }
        }
        model.addAttribute("videos", videos);
         */

        // Recupera i video dal servizio
        List<OroscopoGiornaliero> videos = oroscopoGiornalieroService.findAll();
        model.addAttribute("videos", videos);
        return "oroscopo";
    }




}
