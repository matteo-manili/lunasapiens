package com.lunasapiens.controller;


import com.lunasapiens.TikTokApiClient;
import com.lunasapiens.entity.GestioneApplicazione;
import com.lunasapiens.repository.GestioneApplicazioneRepository;
import com.lunasapiens.service.TikTokOperazioniDbService;
import jakarta.servlet.ServletContext;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.io.InputStream;

@Controller
public class TikTokController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(TikTokController.class);


    private TikTokApiClient tikTokApiClient;
    private ServletContext servletContext;
    private TikTokOperazioniDbService tikTokOperazioniDbService;
    private GestioneApplicazioneRepository gestioneApplicazioneRepository;


    @Autowired
    public TikTokController(TikTokApiClient tikTokApiClient, ServletContext servletContext,
                            JdbcTemplate jdbcTemplate, TikTokOperazioniDbService tikTokOperazioniDbService,
                            GestioneApplicazioneRepository gestioneApplicazioneRepository) {
        this.tikTokApiClient = tikTokApiClient;
        this.servletContext = servletContext;
        this.tikTokOperazioniDbService = tikTokOperazioniDbService;
        this.gestioneApplicazioneRepository = gestioneApplicazioneRepository;
    }



    @GetMapping({"/tiktok-outh", "/tiktok-outh/"})
    public String tikTokCallback(@RequestParam String code, @RequestParam String state, Model model) {
        try{
            logger.info("state: "+state);
            GestioneApplicazione csrfEntity = gestioneApplicazioneRepository.findByName("CSRF_TIKTOK");

            // Verifica lo stato CSRF prima di procedere
            final String storedCSRFState = csrfEntity.getValueString();
            logger.info("db state: "+storedCSRFState);
            if (storedCSRFState == null || !storedCSRFState.equals(state)) {
                // Gestisci l'errore CSRF, ad esempio reindirizzando a una pagina di errore
                model.addAttribute("code", code);
                logger.info("tiktok-outh tikTokCallback controller ERRATO");
                return "tiktok-outh";
            }

            // Eseguire la richiesta per ottenere l'access token utilizzando il code ottenuto
            String json = tikTokApiClient.fetchAccessToken(code);
            logger.info("accessToken: "+json);
            tikTokOperazioniDbService.saveToken_e_refreshToke(json);

        }catch (Exception e){
            e.printStackTrace();
        }

        model.addAttribute("code", code);
        logger.info("tiktok-outh tikTokCallback controller OKK");
        return "tiktok-outh";
    }



    @GetMapping("/tiktok/tiktokz8RIHr0Hiiqijh8czuAojvvevrI58VSV.txt")
    public ResponseEntity<byte[]> tiktokVerificationFile() throws IOException {
        // Carica il file TXT specifico
        String fileName = "tiktokz8RIHr0Hiiqijh8czuAojvvevrI58VSV.txt";
        Resource resource = new ClassPathResource("static/" + fileName);

        // Leggi i dati dalla risorsa utilizzando un InputStream
        InputStream inputStream = resource.getInputStream();
        byte[] fileData = IOUtils.toByteArray(inputStream);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(fileData.length)
                .contentType(MediaType.TEXT_PLAIN)  // Usa il tipo MIME corretto del tuo file
                .body(fileData);
    }

}