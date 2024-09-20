package com.lunasapiens.controller;

import com.lunasapiens.service.TelegramBotService;
import com.lunasapiens.TikTokApiClient;
import com.lunasapiens.repository.GestioneApplicazioneRepository;
import com.lunasapiens.service.TikTokOperazioniDbService;
import jakarta.servlet.ServletContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class ZZZ_TEST_Controller extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(TikTokApiClient.class);


    @GetMapping("/test_1")
    public String ZZZ_TEST_1() {
        try{

            tikTokApiClient.doAutenticazioneTikTok_via_Telegram();

        }catch (Exception e){
            e.printStackTrace();
        }
        return "ZZZ_TEST";
    }

    @GetMapping("/test_2")
    public String ZZZ_TEST_2() {
        try{

            tikTokApiClient.refreshToken();

        }catch (Exception e){
            e.printStackTrace();
        }
        return "ZZZ_TEST";
    }

    @GetMapping("/test_3")
    public String ZZZ_TEST_3() {
        try{

            String accessToken = gestioneApplicazioneRepository.findByName("TOKEN_TIKTOK").getValueString();

            String openId = tikTokApiClient.getUserOpenId(accessToken);

        }catch (Exception e){
            e.printStackTrace();
        }
        return "ZZZ_TEST";
    }

    @GetMapping("/test_4")
    public String ZZZ_TEST_4() {
        try {
            String accessToken = gestioneApplicazioneRepository.findByName("TOKEN_TIKTOK").getValueString();
            String openId = tikTokApiClient.getUserOpenId(accessToken);
            if (openId != null) {
                logger.info("Open ID dell'utente TikTok: " + openId);

                tikTokApiClient.shareVideo(accessToken, openId);

                // Puoi fare qualcosa con l'openId qui
            } else {
                logger.error("Impossibile ottenere l'open ID dell'utente TikTok.");
            }

        } catch (Exception e) {
            logger.error("Errore durante il recupero dell'open ID dell'utente TikTok: " + e.getMessage());
            e.printStackTrace();
        }

        return "ZZZ_TEST";
    }




    private ServletContext servletContext;
    private TikTokOperazioniDbService tikTokOperazioniDbService;
    private JdbcTemplate jdbcTemplate;
    private GestioneApplicazioneRepository gestioneApplicazioneRepository;
    private TelegramBotService telegramBotService;
    private TikTokApiClient tikTokApiClient;

    @Autowired
    public ZZZ_TEST_Controller(ServletContext servletContext, JdbcTemplate jdbcTemplate, TikTokOperazioniDbService tikTokOperazioniDbService,
                               GestioneApplicazioneRepository gestioneApplicazioneRepository, TelegramBotService telegramBotService, TikTokApiClient tikTokApiClient) {

        this.servletContext = servletContext;
        this.jdbcTemplate = jdbcTemplate;
        this.tikTokOperazioniDbService = tikTokOperazioniDbService;
        this.gestioneApplicazioneRepository = gestioneApplicazioneRepository;
        this.telegramBotService = telegramBotService;
        this.tikTokApiClient = tikTokApiClient;
    }







}
