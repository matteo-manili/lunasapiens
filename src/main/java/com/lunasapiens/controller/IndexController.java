package com.lunasapiens.controller;


import com.lunasapiens.*;
import com.lunasapiens.entity.OroscopoGiornaliero;
import com.lunasapiens.service.OroscopoGiornalieroService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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

    @Autowired
    public IndexController(OroscopoGiornalieroService oroscopoGiornalieroService) {
        this.oroscopoGiornalieroService = oroscopoGiornalieroService;
        this.resourceLoader = resourceLoader;
    }


    @GetMapping("/")
    public String index(@RequestParam(name = "name", required = false, defaultValue = "World") String name, Model model) {
        model.addAttribute("message", "Welcome to our dynamic landing page!");
        return "index";
    }


    @GetMapping("/info-privacy")
    public String infoPrivacy(@RequestParam(name = "name", required = false, defaultValue = "World") String name, Model model) {
        model.addAttribute("name", name);
        return "info-privacy";
    }

    @GetMapping("/termini-di-servizio")
    public String terminiDiServizio(@RequestParam(name = "name", required = false, defaultValue = "World") String name, Model model) {
        model.addAttribute("name", name);
        return "termini-di-servizio";
    }


    @GetMapping("/genera-video")
    public String gerneraVideo(Model model) {

        scheduledTasks.creaOroscopoGiornaliero();

        List<OroscopoGiornaliero> listOroscopoGiorn = oroscopoGiornalieroService.findAllByDataOroscopoWithoutVideo(Util.OggiOre12());

        model.addAttribute("videos", listOroscopoGiorn);
        return "oroscopo";
    }



    @GetMapping("/oroscopo")
    public String mostraOroscopo(Model model) {

        logger.info( "Util.OggiRomaOre12(): "+Util.OggiOre12() );
        List<OroscopoGiornaliero> listOroscopoGiorn = oroscopoGiornalieroService.findAllByDataOroscopoWithoutVideo(Util.OggiOre12());

        if( listOroscopoGiorn != null ){
            logger.info( "listOroscopoGiorn.size() :" +listOroscopoGiorn.size() );
        }else{
            logger.info( "listOroscopoGiorn.size() è NULL" );
        }


        model.addAttribute("videos", listOroscopoGiorn);
        return "oroscopo";
    }




    @Cacheable(value = "videoCache", key = "#videoName")
    @GetMapping("/oroscopo-giornaliero/{videoName}")
    @ResponseBody
    public ResponseEntity<Resource> getVideo(@PathVariable String videoName) {
        logger.info("sono in oroscopo-giornaliero videoName: " + videoName);


        OroscopoGiornaliero oroscopoGiornaliero = oroscopoGiornalieroService.findByNomeFileVideo(videoName)
                .orElseThrow(() -> new NoSuchElementException("Video not found with id: " + videoName));


        byte[] videoData = oroscopoGiornaliero.getVideo();
        ByteArrayResource resource = new ByteArrayResource(videoData);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        // Imposta Content-Disposition su "inline" per riprodurre il video direttamente nella pagina
        headers.setContentDisposition(ContentDisposition.builder("inline").filename(videoName).build());
        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);


    }



}
