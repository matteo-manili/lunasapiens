package com.lunasapiens.controller;

import com.lunasapiens.Constants;
import com.lunasapiens.config.FacebookConfig;
import com.lunasapiens.repository.DatabaseMaintenanceRepository;
import com.lunasapiens.service.TelegramBotService;
import com.lunasapiens.TikTokApiClient;
import com.lunasapiens.repository.GestioneApplicazioneRepository;
import com.lunasapiens.service.TikTokOperazioniDbService;
import com.lunasapiens.zodiac.ServizioOroscopoDelGiorno;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// #######################################################################################
// ############################# TEST ####################################################
// #######################################################################################

@Controller
public class TESTController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(TikTokApiClient.class);

    @Autowired
    private FacebookConfig facebookConfig;

    @Autowired
    ServizioOroscopoDelGiorno servizioOroscopoDelGiorno;

    @Autowired
    private DatabaseMaintenanceRepository databaseMaintenanceRepository;




    @GetMapping("/database-clear-oroscopo-video")
    public String databaseClearOrosocpoVideo(RedirectAttributes redirectAttributes) {
        if (!isMatteoManilIdUser()) {
            redirectAttributes.addFlashAttribute(Constants.INFO_ERROR, "Accesso negato: non hai i permessi per visualizzare questa pagina.");
            return "redirect:/error";
        }
        logger.info("eseguo databaseMaintenanceRepository.deleteOldOroscopoRecords()...");
        databaseMaintenanceRepository.deleteOldOroscopoRecords();
        logger.info("fine esecuzione databaseMaintenanceRepository.deleteOldOroscopoRecords()");
        return "redirect:/";
    }



    @GetMapping("/test")
    public String test(Model model) {

        Map<String, Object[]> planets = new HashMap<>();
        planets.put("Sun", new Object[]{303.53333333333336});
        planets.put("Moon", new Object[]{163.18333333333334});
        planets.put("Mercury", new Object[]{318.3333333333333});
        planets.put("Venus", new Object[]{285.43333333333334});
        planets.put("Mars", new Object[]{318.6666666666667});
        planets.put("Jupiter", new Object[]{190.38333333333333});
        planets.put("Saturn", new Object[]{189.75, -0.2});
        planets.put("Uranus", new Object[]{239.36666666666667});
        planets.put("Neptune", new Object[]{263.81666666666666});
        planets.put("Pluto", new Object[]{204.33333333333334});
        planets.put("NNode", new Object[]{131.3});
        planets.put("SNode", new Object[]{311.3});
        planets.put("Lilith", new Object[]{212.75});
        planets.put("Chiron", new Object[]{43.4});
        //planets.put("Fortune", new Object[]{330.18333333333334}); // non lo uso

        model.addAttribute("planets", planets);

        List<Integer> cusps = Arrays.asList(
                110, 129
        );
        model.addAttribute("cusps", cusps);

        return "test";
    }


    /**
     * lo uso solo per test
     * Reindirizza alla home page e segnala che la risorsa non esiste più
     */
    @GetMapping("/crea-oroscopo-giornaliero")
    public String creaOroscopoGiornaliero(RedirectAttributes redirectAttributes) {
        if (!isMatteoManilIdUser()) {
            redirectAttributes.addFlashAttribute(Constants.INFO_ERROR, "Accesso negato: non hai i permessi per visualizzare questa pagina.");
            return "redirect:/error";
        }
        //scheduledTasks.test_Oroscopo_Segni_Transiti_Aspetti();
        servizioOroscopoDelGiorno.creaOroscopoGiornaliero();

        return "redirect:/";
    }

    @GetMapping("/test_facebook")
    public String testFacebook(RedirectAttributes redirectAttributes) {
        try {
            if (!isMatteoManilIdUser()) {
                redirectAttributes.addFlashAttribute(Constants.INFO_ERROR, "Accesso negato: non hai i permessi per visualizzare questa pagina.");
                return "redirect:/error";
            }
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
            return "redirect:/index";

        } catch (FacebookOAuthException e) {
            logger.error("FacebookOAuthException occurred: ", e);
            return "redirect:/error";
        } catch (Exception e) {
            logger.error("Exception occurred: ", e);
            return "redirect:/error";
        }
    }


    @GetMapping("/test_1")
    public String ZZZ_TEST_1(RedirectAttributes redirectAttributes) {
        try{
            if (!isMatteoManilIdUser()) {
                redirectAttributes.addFlashAttribute(Constants.INFO_ERROR, "Accesso negato: non hai i permessi per visualizzare questa pagina.");
                return "redirect:/error";
            }
            tikTokApiClient.doAutenticazioneTikTok_via_Telegram();

        }catch (Exception e){
            e.printStackTrace();
        }
        return "redirect:/test";
    }


    @GetMapping("/test_2")
    public String ZZZ_TEST_2(RedirectAttributes redirectAttributes) {
        try{
            if (!isMatteoManilIdUser()) {
                redirectAttributes.addFlashAttribute(Constants.INFO_ERROR, "Accesso negato: non hai i permessi per visualizzare questa pagina.");
                return "redirect:/error";
            }
            tikTokApiClient.refreshToken();

        }catch (Exception e){
            e.printStackTrace();
        }
        return "redirect:/test";
    }


    @GetMapping("/test_3")
    public String ZZZ_TEST_3(RedirectAttributes redirectAttributes) {
        try{
            if (!isMatteoManilIdUser()) {
                redirectAttributes.addFlashAttribute(Constants.INFO_ERROR, "Accesso negato: non hai i permessi per visualizzare questa pagina.");
                return "redirect:/error";
            }
            String accessToken = gestioneApplicazioneRepository.findByName("TOKEN_TIKTOK").getValueString();
            String openId = tikTokApiClient.getUserOpenId(accessToken);

        }catch (Exception e){
            e.printStackTrace();
        }
        return "redirect:/test";
    }


    @GetMapping("/test_4")
    public String ZZZ_TEST_4(RedirectAttributes redirectAttributes) {
        try {
            if (!isMatteoManilIdUser()) {
                redirectAttributes.addFlashAttribute(Constants.INFO_ERROR, "Accesso negato: non hai i permessi per visualizzare questa pagina.");
                return "redirect:/error";
            }
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
        return "redirect:/test";
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
