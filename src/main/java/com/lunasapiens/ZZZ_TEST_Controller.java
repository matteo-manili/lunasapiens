package com.lunasapiens;

import com.lunasapiens.entity.GestioneApplicazione;
import com.lunasapiens.repository.GestioneApplicazioneRepository;
import com.lunasapiens.service.OperazioniDbTikTokService;
import jakarta.servlet.ServletContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.util.UriComponentsBuilder;


@Controller
public class ZZZ_TEST_Controller {

    private static final Logger logger = LoggerFactory.getLogger(TikTokApiClient.class);


    @GetMapping("/test")
    public String ZZZ_TEST() {
        try{

            doAutenticazioneTikTok();

        }catch (Exception e){
            e.printStackTrace();
        }
        return "ZZZ_TEST";
    }



    private ServletContext servletContext;
    private OperazioniDbTikTokService operazioniDbTikTokService;
    private JdbcTemplate jdbcTemplate;
    private GestioneApplicazioneRepository gestioneApplicazioneRepository;
    private TelegramBot telegramBot;

    @Autowired
    public ZZZ_TEST_Controller(ServletContext servletContext, JdbcTemplate jdbcTemplate, OperazioniDbTikTokService operazioniDbTikTokService,
                           GestioneApplicazioneRepository gestioneApplicazioneRepository, TelegramBot telegramBot) {

        this.servletContext = servletContext;
        this.jdbcTemplate = jdbcTemplate;
        this.operazioniDbTikTokService = operazioniDbTikTokService;
        this.gestioneApplicazioneRepository = gestioneApplicazioneRepository;
        this.telegramBot = telegramBot;
    }

    @Autowired
    private Environment env;


    public void doAutenticazioneTikTok(){

        // Creazione del CSRF state token
        String csrfState = generateCSRFState();

        // Costruzione dell'URL di autorizzazione
        String authorizationUri = UriComponentsBuilder.fromHttpUrl( env.getProperty("api.tiktok.authorizationUrl"))
                .queryParam("client_key", env.getProperty("api.tiktok.clientKey"))
                .queryParam("scope", env.getProperty("api.tiktok.scope") )
                .queryParam("response_type", "code")
                .queryParam("redirect_uri", env.getProperty("api.tiktok.redirectUri"))
                .queryParam("state", csrfState)
                .build().toUriString();

        //TelegramBot telegramBot = new TelegramBot();
        telegramBot.inviaMessaggio("link autenticazione tiktok: "+authorizationUri);

        // Apertura dell'URL nel browser o integrazione con il tuo frontend
        System.out.println("Apri l'URL nel browser: " + authorizationUri);

        saveCSRFState(csrfState);
        // Ora puoi gestire la risposta dell'utente dopo il login e ottenere l'access token
        // ...
    }

    private void saveCSRFState(String csrfState) {
        // Salva lo stato CSRF nelle variabili globali di Spring (ServletContext)
        logger.info("salvo la csrfState nel database: " + csrfState);

        try {
            //operazioniDbTikTokService.saveValue("columnName", "exampleValue");

            // Recupera l'entità con name uguale a "CSRF_TIKTOK" dal database
            GestioneApplicazione csrfEntity = gestioneApplicazioneRepository.findByName("CSRF_TIKTOK");

            if (csrfEntity != null) {
                // Aggiorna i valori di valueString e valueNumber
                csrfEntity.setValueString( csrfState );

                // Salva l'entità aggiornata nel database
                gestioneApplicazioneRepository.save(csrfEntity);
                logger.info("Value updated in the database!");
            } else {
                logger.info("Record with name 'CSRF_TIKTOK' not found in the database.");
            }
        } catch (Exception e) {
            logger.info("Error updating value in the database: " + e.getMessage());
        }

    }

    private static String generateCSRFState() {
        // Implementa la logica per generare un CSRF state token univoco
        // Puoi utilizzare un generatore casuale o la logica desiderata
        return Integer.toString((int) (Math.random() * 1000000)); // Esempio di generazione di un numero casuale come token
        //return "csrf_token";
    }






}
