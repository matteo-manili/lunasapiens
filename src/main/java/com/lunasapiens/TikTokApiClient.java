package com.lunasapiens;


import com.lunasapiens.entity.GestioneApplicazione;
import com.lunasapiens.repository.GestioneApplicazioneRepository;
import com.lunasapiens.service.TikTokOperazioniDbService;
import jakarta.servlet.ServletContext;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import org.slf4j.LoggerFactory;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

@Component
public class TikTokApiClient {

    private static final Logger logger = LoggerFactory.getLogger(TikTokApiClient.class);

    private ServletContext servletContext;
    private TikTokOperazioniDbService tikTokOperazioniDbService;
    private JdbcTemplate jdbcTemplate;
    private GestioneApplicazioneRepository gestioneApplicazioneRepository;
    private TelegramBot telegramBot;

    @Autowired
    public TikTokApiClient(ServletContext servletContext, JdbcTemplate jdbcTemplate, TikTokOperazioniDbService tikTokOperazioniDbService,
                           GestioneApplicazioneRepository gestioneApplicazioneRepository, TelegramBot telegramBot) {

        this.servletContext = servletContext;
        this.jdbcTemplate = jdbcTemplate;
        this.tikTokOperazioniDbService = tikTokOperazioniDbService;
        this.gestioneApplicazioneRepository = gestioneApplicazioneRepository;
        this.telegramBot = telegramBot;
    }

    @Autowired
    private Environment env;

    private final String BASE_URL = "https://open.tiktokapis.com";
    private final String ACCESS_TOKEN = gestioneApplicazioneRepository.findByName("TOKEN_TIKTOK").getValueString();
    private final String USER_ID = "_000ZcdXGKAidjCzF6YAktD42NIR7lf2MSed"; // Sostituisci con l'ID utente TikTok



    public void postVideoToTikTok() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + ACCESS_TOKEN);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Inizializza la richiesta per ottenere informazioni sul creatore
        String creatorInfoUrl = BASE_URL + "/v2/post/publish/creator_info/query/";
        HttpEntity<String> creatorInfoRequest = new HttpEntity<>(headers);
        ResponseEntity<String> creatorInfoResponse = new RestTemplate().exchange(
                creatorInfoUrl, HttpMethod.POST, creatorInfoRequest, String.class);

        // Estrai informazioni sul creatore dal response (es. creator_username, creator_nickname)

        // Inizializza la richiesta per pubblicare il video
        String postVideoUrl = BASE_URL + "/v2/post/publish/video/init/";
        String videoSourceUrl = "https://lunasapiens.com/static/video_playa.mp4"; // Sostituisci con l'URL del tuo video o il percorso del file locale
        String requestBody = "{\n" +
                "  \"post_info\": {\n" +
                "    \"title\": \"Titolo del video\",\n" +
                "    \"privacy_level\": \"MUTUAL_FOLLOW_FRIENDS\",\n" +
                "    \"disable_duet\": false,\n" +
                "    \"disable_comment\": true,\n" +
                "    \"disable_stitch\": false,\n" +
                "    \"video_cover_timestamp_ms\": 1000\n" +
                "  },\n" +
                "  \"source_info\": {\n" +
                "      \"source\": \"PULL_FROM_URL\",\n" +
                "      \"video_url\": \"" + videoSourceUrl + "\"\n" +
                "  }\n" +
                "}";
        HttpEntity<String> postVideoRequest = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> postVideoResponse = new RestTemplate().exchange(
                postVideoUrl, HttpMethod.POST, postVideoRequest, String.class);

        // Estrai l'ID di pubblicazione e l'URL di upload dal response (es. publish_id, upload_url)

        // Se stai usando source=FILE_UPLOAD, invia il video ai server di TikTok
        // Usa l'upload_url e il publish_id ottenuti dalla risposta precedente
        // Assicurati di implementare la gestione della trasmissione del file video

        // Puoi implementare anche la verifica dello stato dell'upload utilizzando il Get Post Status endpoint
    }



    public void doAutenticazioneTikTok_via_Telegram(){
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
    }

    private void saveCSRFState(String csrfState) {
        // Salva lo stato CSRF nelle variabili globali di Spring (ServletContext)
        logger.info("salvo la csrfState nel database: " + csrfState);

        try {
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


    /**
     * attraverso quuesto motodo, quando scade il token per fare le operazioni su tiktok, passando il refresh_token,
     * genera un nuovo token senza chiedere l'autenticazione, che può essere usato per continuare le operazioni come il precedente.
     */
    public void refreshToken(){
        try{
            logger.info("sono in refreshToken");
            GestioneApplicazione tokenRefreshTiktok = gestioneApplicazioneRepository.findByName("TOKEN_REFRESH_TIKTOK");
            String json = tikTokOperazioniDbService.refreshAccessToken( tokenRefreshTiktok.getValueString() );
            logger.info("json refreshAccessToken:"+ json);
            tikTokOperazioniDbService.saveToken_e_refreshToke(json);
        } catch (Exception e) {
            logger.info("Error updating value in the database: " + e.getMessage());
            e.printStackTrace();
        }
    }



}
