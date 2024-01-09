package com.lunasapiens;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lunasapiens.entity.GestioneApplicazione;
import com.lunasapiens.repository.GestioneApplicazioneRepository;
import com.lunasapiens.service.OperazioniDbTikTokService;
import jakarta.servlet.ServletContext;
import org.apache.commons.io.IOUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class TikTokController {

    private static final Logger logger = LoggerFactory.getLogger(TikTokController.class);


    private TikTokApiClient tikTokApiClient;
    private ServletContext servletContext;
    private OperazioniDbTikTokService operazioniDbTikTokService;
    private JdbcTemplate jdbcTemplate;
    private GestioneApplicazioneRepository gestioneApplicazioneRepository;


    @Autowired
    public TikTokController(TikTokApiClient tikTokApiClient, ServletContext servletContext,
                            JdbcTemplate jdbcTemplate, OperazioniDbTikTokService operazioniDbTikTokService,
                            GestioneApplicazioneRepository gestioneApplicazioneRepository) {
        this.tikTokApiClient = tikTokApiClient;
        this.servletContext = servletContext;
        this.jdbcTemplate = jdbcTemplate;
        this.operazioniDbTikTokService = operazioniDbTikTokService;
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
            String json = fetchAccessToken(code);
            logger.info("accessToken: "+json);

            saveToken_e_refreshToke(json);

        }catch (Exception e){
            e.printStackTrace();
        }

        model.addAttribute("code", code);
        logger.info("tiktok-outh tikTokCallback controller OKK");
        return "tiktok-outh";
    }



    private void saveToken_e_refreshToke(String json){

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(json);
            String accessToken = jsonNode.get("access_token").asText();
            String refreshToken = jsonNode.get("refresh_token").asText();
            logger.info("Access Token: " + accessToken);
            logger.info("Refresh Token: " + refreshToken);


            // Recupera l'entità con name uguale a "CSRF_TIKTOK" dal database
            GestioneApplicazione tokenTiktok = gestioneApplicazioneRepository.findByName("TOKEN_TIKTOK");
            if (tokenTiktok != null) {
                tokenTiktok.setValueString( accessToken );
                gestioneApplicazioneRepository.save(tokenTiktok);
                logger.info("Value accessToken updated in the database!");
            } else {
                logger.info("Record with name 'TOKEN_TIKTOK' not found in the database.");
            }

            // Recupera l'entità con name uguale a "CSRF_TIKTOK" dal database
            GestioneApplicazione tokenRefreshTiktok = gestioneApplicazioneRepository.findByName("TOKEN_REFRESH_TIKTOK");
            if (tokenRefreshTiktok != null) {
                tokenRefreshTiktok.setValueString( refreshToken );
                gestioneApplicazioneRepository.save(tokenRefreshTiktok);
                logger.info("Value refreshToken updated in the database!");
            } else {
                logger.info("Record with name 'TOKEN_REFRESH_TIKTOK' not found in the database.");
            }


        } catch (Exception e) {
            logger.info("Error updating value in the database: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @Value("${api.tiktok.clientKey}")
    private String clientKey;

    @Value("${api.tiktok.clientSecret}")
    private String clientSecret;

    @Value("${api.tiktok.redirectUri}")
    private String redirectUri;

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