package com.lunasapiens;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.lunasapiens.entity.GestioneApplicazione;
import com.lunasapiens.repository.GestioneApplicazioneRepository;
import com.lunasapiens.service.TikTokOperazioniDbService;
import jakarta.servlet.ServletContext;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.UrlResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;
import org.slf4j.LoggerFactory;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Component
public class TikTokApiClient {

    private static final Logger logger = LoggerFactory.getLogger(TikTokApiClient.class);

    private ServletContext servletContext;
    private TikTokOperazioniDbService tikTokOperazioniDbService;
    private JdbcTemplate jdbcTemplate;
    private GestioneApplicazioneRepository gestioneApplicazioneRepository;
    private TelegramBotClient telegramBotClient;

    @Autowired
    public TikTokApiClient(ServletContext servletContext, JdbcTemplate jdbcTemplate, TikTokOperazioniDbService tikTokOperazioniDbService,
                           GestioneApplicazioneRepository gestioneApplicazioneRepository, TelegramBotClient telegramBotClient) {

        this.servletContext = servletContext;
        this.jdbcTemplate = jdbcTemplate;
        this.tikTokOperazioniDbService = tikTokOperazioniDbService;
        this.gestioneApplicazioneRepository = gestioneApplicazioneRepository;
        this.telegramBotClient = telegramBotClient;
    }

    @Autowired
    private Environment env;



    @Value("${api.tiktok.clientKey}")
    private String clientKey;

    @Value("${api.tiktok.clientSecret}")
    private String clientSecret;

    @Value("${api.tiktok.redirectUri}")
    private String redirectUri;


    //private static final String USER_ACCESS_TOKEN = "your_user_access_token";
    //private String BASE_URL = "https://open.tiktokapis.com";
    //private String USER_ID = "_000ZcdXGKAidjCzF6YAktD42NIR7lf2MSed"; // Sostituisci con l'ID utente TikTok


    public static String initializeVideoUpload_old(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = "https://open.tiktokapis.com/v2/post/publish/video/init/";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        String requestBody = "{\n" +
                "  \"post_info\": {\n" +
                "    \"title\": \"This is my TikTok video\",\n" +
                "    \"privacy_level\": \"MUTUAL_FOLLOW_FRIENDS\",\n" +
                "    \"disable_duet\": false,\n" +
                "    \"disable_comment\": true,\n" +
                "    \"disable_stitch\": false,\n" +
                "    \"video_cover_timestamp_ms\": 1000\n" +
                "  },\n" +
                "  \"source_info\": {\n" +
                "    \"source\": \"FILE_UPLOAD\",\n" +
                "    \"video_size\": 1490000,  // Dimensione del video in byte\n" +
                "    \"chunk_size\": 1000000,  // Dimensione di ogni chunk in byte\n" +
                "    \"total_chunk_count\": 2    // Numero totale di chunk\n" +
                "  }\n" +
                "}";

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(apiUrl, requestEntity, String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode responseJson;
        try {
            responseJson = objectMapper.readTree(responseEntity.getBody());

            logger.info("Risultato JSON: " + responseJson.toString());

            String uploadId = responseJson.get("upload_id").asText();
            String uploadToken = responseJson.get("upload_token").asText();

            // Puoi estrarre altre informazioni se necessario

            // Restituisci un oggetto contenente i valori estratti
            //return new VideoUploadResponse(uploadId, uploadToken);
            return "";

        } catch (JsonProcessingException e) {
            // Gestisci l'eccezione come preferisci, ad esempio, loggando un messaggio di errore
            e.printStackTrace(); // o usa un logger per registrare l'errore in modo appropriato
            return null; // o lancia un'eccezione personalizzata se necessario
        }

        // Parse the response to get the upload URL
        // Note: You should add proper error handling here
        //return "https://open-upload.tiktokapis.com/video/?upload_id=67890&upload_token=Xza123";
    }


    private static String API_URL = "https://open.tiktokapis.com/v2/post/publish/video/init/";
    //private static String API_URL = "https://open.tiktokapis.com/v2/post/publish/content/init/";


    public static String initializeVideoUpload(String accessToken)  {

        // Creare l'oggetto JSON per la richiesta
        ObjectMapper objectMapper = new ObjectMapper();
        //String requestBody = "{\"post_info\": { /* ... dati della richiesta ... */ }, \"source_info\": { /* ... dati della richiesta ... */ }}";

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
                "    \"source\": \"FILE_UPLOAD\",\n" +
                "    \"video_size\": 50000123,\n" +
                "    \"chunk_size\": 10000000,\n" +
                "    \"total_chunk_count\": 5\n" +
                "  }\n" +
                "}";


        // Effettuare la chiamata API
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth( accessToken );

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> responseEntity = restTemplate.exchange(API_URL, HttpMethod.POST, requestEntity, String.class);


        System.out.println("Risposta JSON:\n" + responseEntity.getBody().toString());

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            // Analizzare la risposta JSON
            try {
                JsonNode rootNode = objectMapper.readTree(responseEntity.getBody());
                JsonNode dataNode = rootNode.path("data");

                // Estrarre i dati necessari dalla risposta
                String publishId = dataNode.path("publish_id").asText();
                String uploadUrl = dataNode.path("upload_url").asText();

                // Utilizzare publishId e uploadUrl secondo le istruzioni della documentazione
                // ...

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // Gestire errori HTTP
            System.out.println("Errore nella chiamata API: " + responseEntity.getStatusCodeValue());
        }


        return "";
    }



    public static void uploadVideo(String uploadUrl, String videoPath) throws IOException {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        FileSystemResource videoFile = new FileSystemResource(new File(videoPath));

        headers.setContentLength(videoFile.contentLength());

        HttpEntity<FileSystemResource> requestEntity = new HttpEntity<>(videoFile, headers);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(uploadUrl, requestEntity, String.class);

        // Handle the response as needed
        // Note: You should add proper error handling here
        System.out.println(responseEntity.getBody());
    }

    // ################################################################


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

            logger.info("responseBody: "+responseBody);

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


    public String getUserOpenId(String accessToken) {
        try {
            // Inizializza RestTemplate
            RestTemplate restTemplate = new RestTemplate();

            // Crea l'header con il token di accesso
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);

            // Imposta i parametri della richiesta
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("https://open.tiktokapis.com/v2/user/info/")
                    .queryParam("fields", "open_id");

            // Crea l'oggetto HttpEntity con l'header
            HttpEntity<String> requestEntity = new HttpEntity<>(headers);

            // Esegui la richiesta
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    builder.toUriString(),
                    HttpMethod.GET,
                    requestEntity,
                    String.class
            );

            // Ottieni la risposta
            String responseBody = responseEntity.getBody();
            //logger.info("responseBody: " + responseBody);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode dataNode = rootNode.path("data");
            JsonNode userNode = dataNode.path("user");
            String openId = userNode.path("open_id").asText();

            return openId;
        } catch (Exception e) {
            logger.error("Errore durante il recupero dell'open_id da TikTok: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }


    @Autowired
    private RestTemplate restTemplate;

    // video di 54 secondi. sembra che tiktok non accetta video più lunghi
    //https://www.lunasapiens.com/oroscopo-giornaliero/2024-03-15_1.mp4


    public String initializeVideoUpload(String accessToken, String openId) {
        try {
            String apiUrl = "https://open-api.tiktok.com/share/video/upload/";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.setBearerAuth(accessToken);

            //http://localhost:8081/oroscopo-giornaliero/2024-03-09_1.mp4
            //https://www.lunasapiens.com/oroscopo-giornaliero/2024-03-09_1.mp4
            // Recupera il video dal metodo del controller
            ResponseEntity<byte[]> videoResponse = restTemplate.exchange(
                    "https://www.lunasapiens.com/oroscopo-giornaliero/2024-03-15_1.mp4", // Sostituisci con l'URL del metodo del controller
                    HttpMethod.GET,
                    null,
                    byte[].class
            );

            // Verifica se il video è stato recuperato correttamente
            if (videoResponse.getStatusCode() == HttpStatus.OK) {
                // Crea un ByteArrayResource dal corpo della risposta
                ByteArrayResource videoResource = new ByteArrayResource(videoResponse.getBody()) {
                    @Override
                    public String getFilename() {
                        return "2024-03-15_1.mp4"; // Sostituisci con il nome del video
                    }
                };

                // Aggiungi il video come parte del corpo della richiesta
                MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
                body.add("video", videoResource);

                // Aggiungi l'open_id ai parametri della richiesta
                UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrl)
                        .queryParam("open_id", openId);

                HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
                ResponseEntity<String> responseEntity = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, requestEntity, String.class);

                if (responseEntity.getStatusCode() == HttpStatus.OK) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode rootNode = objectMapper.readTree(responseEntity.getBody());
                    JsonNode dataNode = rootNode.path("data");
                    String shareId = dataNode.path("share_id").asText();
                    return shareId;
                } else {
                    // Handle error response
                    return null;
                }
            } else {
                // Handle error response
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
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
        telegramBotClient.inviaMessaggio("link autenticazione tiktok: "+authorizationUri);

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
