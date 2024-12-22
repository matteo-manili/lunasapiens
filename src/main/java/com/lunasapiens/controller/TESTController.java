package com.lunasapiens.controller;

import com.lunasapiens.config.FacebookConfig;
import com.lunasapiens.service.TelegramBotService;
import com.lunasapiens.TikTokApiClient;
import com.lunasapiens.repository.GestioneApplicazioneRepository;
import com.lunasapiens.service.TikTokOperazioniDbService;
import com.restfb.exception.FacebookOAuthException;
import jakarta.servlet.ServletContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.restfb.*;
import com.restfb.types.FacebookType;
import com.restfb.types.Page;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

// #######################################################################################
// ############################# TEST ####################################################
// #######################################################################################

@Controller
public class TESTController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(TikTokApiClient.class);

    @Autowired
    private FacebookConfig facebookConfig;


    @GetMapping("/test")
    public String test(Model model) {

            return "test";
    }



    @GetMapping("/test_facebook")
    public String testFacebook(Model model) {
        try {
            String pageID = facebookConfig.getPageId();
            String appId = facebookConfig.getAppId();
            String appSecret = facebookConfig.getAppSecret();
            // Ottieni il token di accesso dell'app
            AccessToken appAccessToken = new DefaultFacebookClient(Version.LATEST)
                    .obtainAppAccessToken(appId, appSecret);
            // Crea un client Facebook con il token dell'app
            FacebookClient facebookClient = new DefaultFacebookClient(appAccessToken.getAccessToken(), Version.LATEST);
            logger.info("appAccessToken.getAccessToken(): "+appAccessToken. getAccessToken());
            // Ottieni la pagina
            Page page = facebookClient.fetchObject(pageID, Page.class, Parameter.with("fields", "access_token"));
            // Ottieni il token di accesso della pagina
            String pageAccessToken = page.getAccessToken();
            // Crea un client Facebook per la pagina con il token della pagina
            FacebookClient pageClient = new DefaultFacebookClient(pageAccessToken, Version.LATEST);
            // Pubblica un messaggio sulla bacheca della pagina
            pageClient.publish(pageID + "/feed", FacebookType.class, Parameter.with("message", "1: Hello, facebook World!"));
            return "index";

        } catch (FacebookOAuthException e) {
            logger.error("FacebookOAuthException occurred: ", e);
            return "error";
        } catch (Exception e) {
            logger.error("Exception occurred: ", e);
            return "error";
        }
    }


    @GetMapping("/test_1")
    public String ZZZ_TEST_1() {
        try{

            tikTokApiClient.doAutenticazioneTikTok_via_Telegram();

        }catch (Exception e){
            e.printStackTrace();
        }
        return "test";
    }


    @GetMapping("/test_2")
    public String ZZZ_TEST_2() {
        try{

            tikTokApiClient.refreshToken();

        }catch (Exception e){
            e.printStackTrace();
        }
        return "test";
    }


    @GetMapping("/test_3")
    public String ZZZ_TEST_3() {
        try{

            String accessToken = gestioneApplicazioneRepository.findByName("TOKEN_TIKTOK").getValueString();

            String openId = tikTokApiClient.getUserOpenId(accessToken);

        }catch (Exception e){
            e.printStackTrace();
        }
        return "test";
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

        return "test";
    }




    private ServletContext servletContext;
    private TikTokOperazioniDbService tikTokOperazioniDbService;
    private JdbcTemplate jdbcTemplate;
    private GestioneApplicazioneRepository gestioneApplicazioneRepository;
    private TelegramBotService telegramBotService;
    private TikTokApiClient tikTokApiClient;

    @Autowired
    public TESTController(ServletContext servletContext, JdbcTemplate jdbcTemplate, TikTokOperazioniDbService tikTokOperazioniDbService,
                          GestioneApplicazioneRepository gestioneApplicazioneRepository, TelegramBotService telegramBotService, TikTokApiClient tikTokApiClient) {

        this.servletContext = servletContext;
        this.jdbcTemplate = jdbcTemplate;
        this.tikTokOperazioniDbService = tikTokOperazioniDbService;
        this.gestioneApplicazioneRepository = gestioneApplicazioneRepository;
        this.telegramBotService = telegramBotService;
        this.tikTokApiClient = tikTokApiClient;
    }







}
