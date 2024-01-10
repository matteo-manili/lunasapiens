package com.lunasapiens;


import com.lunasapiens.entity.GestioneApplicazione;
import com.lunasapiens.repository.GestioneApplicazioneRepository;
import com.lunasapiens.service.TikTokOperazioniDbService;
import jakarta.servlet.ServletContext;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;
import org.slf4j.LoggerFactory;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    private final String USER_ID = "_000ZcdXGKAidjCzF6YAktD42NIR7lf2MSed"; // Sostituisci con l'ID utente TikTok

    @Value("${api.tiktok.clientKey}")
    private String clientKey;

    @Value("${api.tiktok.clientSecret}")
    private String clientSecret;

    @Value("${api.tiktok.redirectUri}")
    private String redirectUri;



    /*
    {"data":{"creator_avatar_url":"https://p16-sign-va.tiktokcdn.com/tos-maliva-avt-0068/7318280529291837445~c5_168x168.webp?lk3s=a5d48078\u0026x-expires=1705078800\u0026x-signature=q80J1t5ictUlp%2BivH476IXCS8ts%3D"
    ,"creator_nickname":"AlexDeLarge","creator_username":"alexdelargeit","duet_disabled":false,"max_video_post_duration_sec":600,"privacy_level_options":["PUBLIC_TO_EVERYONE","MUTUAL_FOLLOW_FRIENDS","SELF_ONLY"],
    "stitch_disabled":false,"comment_disabled":false},"error":{"code":"ok","message":"","log_id":"20240110174023A14E100A8B5ABC5F3294"}}
     */
    public void TikTokCreatorInfoRequest() {
        try {
            // Ottieni l'access token da qualche repository (sostituisci con la tua logica)
            String accessToken = gestioneApplicazioneRepository.findByName("TOKEN_TIKTOK").getValueString();
            logger.info("accessToken: " + accessToken);

            // Inizializza RestTemplate
            RestTemplate restTemplate = new RestTemplate();

            // Crea l'header con il token di accesso
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Crea l'oggetto HttpEntity con l'header
            HttpEntity<String> requestEntity = new HttpEntity<>(headers);

            // Esegui la richiesta
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    "https://open.tiktokapis.com/v2/post/publish/creator_info/query/",
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            // Ottieni la risposta
            String responseBody = responseEntity.getBody();

            logger.info(responseBody);

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 401) {
                logger.error("Errore 401 durante la richiesta a TikTok. Token di accesso non valido o scaduto.");
            } else {
                logger.error("Errore durante la richiesta a TikTok: " + e.getStatusCode().value() + " " + e.getStatusText());
                logger.error("Dettagli: " + e.getResponseBodyAsString());
            }
            e.printStackTrace();
        } catch (Exception e) {
            logger.error("Errore generico durante la richiesta a TikTok: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public void postVideoToTikTok() {

        try {
            HttpHeaders headers = new HttpHeaders();
            String ACCESS_TOKEN = gestioneApplicazioneRepository.findByName("TOKEN_TIKTOK").getValueString();
            headers.set("Authorization", "Bearer " + ACCESS_TOKEN);
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Inizializza la richiesta per ottenere informazioni sul creatore
            String creatorInfoUrl = BASE_URL + "/v2/user/" + USER_ID + "/info/";

            HttpEntity<String> creatorInfoRequest = new HttpEntity<>(headers);
            ResponseEntity<String> creatorInfoResponse = new RestTemplate().exchange(
                    creatorInfoUrl, HttpMethod.GET, creatorInfoRequest, String.class);

            // Estrai informazioni sul creatore dal response (es. creator_username, creator_nickname)

            // Inizializza la richiesta per pubblicare il video
            String postVideoUrl = BASE_URL + "/v2/post/publish/video/init/";
            String videoSourceUrl = "https://www.lunasapiens.com/static/video_playa.mp4"; // Sostituisci con l'URL del tuo video o il percorso del file locale
            String requestBody = "{\n" +
                    "  \"post_info\": {\n" +
                    "    \"title\": \"Titolo del video\",\n" +
                    "    \"description\": \"Descrizione del video qui\",\n" +
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
        } catch (HttpClientErrorException e) {
            System.err.println("Errore durante la richiesta a TikTok: " + e.getRawStatusCode() + " " + e.getStatusText());
            System.err.println("Dettagli: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            System.err.println("Errore generico durante la richiesta a TikTok: " + e.getMessage());
        }
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
            String json = refreshAccessToken( tokenRefreshTiktok.getValueString() );
            logger.info("json refreshAccessToken:"+ json);
            tikTokOperazioniDbService.saveToken_e_refreshToke(json);
        } catch (Exception e) {
            logger.info("Error updating value in the database: " + e.getMessage());
            e.printStackTrace();
        }
    }



    public String fetchAccessToken(String authorizationCode) throws IOException, URISyntaxException {
        List<NameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("client_key", clientKey));
        parameters.add(new BasicNameValuePair("client_secret", clientSecret));
        parameters.add(new BasicNameValuePair("code", authorizationCode));
        parameters.add(new BasicNameValuePair("grant_type", "authorization_code"));
        parameters.add(new BasicNameValuePair("redirect_uri", redirectUri));

        URI uri = new URIBuilder("https://open.tiktokapis.com/v2/oauth/token/").build();
        return executeTokenRequest(uri, parameters);

    }

    /**
     * attraverso quuesto motodo, quando scade il token per fare le operazioni su tiktok, passando il refresh_token,
     * genera un nuovo token senza chiedere l'autenticazione, che può essere usato per continuare le operazioni come il precedente.
     * @param refreshToken
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    public String refreshAccessToken(String refreshToken) throws IOException, URISyntaxException {
        List<NameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("client_key", clientKey));
        parameters.add(new BasicNameValuePair("client_secret", clientSecret));
        parameters.add(new BasicNameValuePair("grant_type", "refresh_token"));
        parameters.add(new BasicNameValuePair("refresh_token", refreshToken));

        URI uri = new URIBuilder("https://open.tiktokapis.com/v2/oauth/token/").build();
        return executeTokenRequest(uri, parameters);
    }


    private String executeTokenRequest(URI uri, List<NameValuePair> parameters) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(uri);
            request.setHeader("Content-Type", "application/x-www-form-urlencoded");
            request.setEntity(new UrlEncodedFormEntity(parameters));

            logger.info(  parameters.toString());

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                return EntityUtils.toString(response.getEntity());
            }
        }
    }



}
