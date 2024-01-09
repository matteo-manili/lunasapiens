package com.lunasapiens.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lunasapiens.TikTokController;
import com.lunasapiens.entity.GestioneApplicazione;
import com.lunasapiens.repository.GestioneApplicazioneRepository;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@Service
public class OperazioniDbTikTokService {

    private static final Logger logger = LoggerFactory.getLogger(OperazioniDbTikTokService.class);

    private final GestioneApplicazioneRepository gestioneApplicazioneRepository;

    @Autowired
    public OperazioniDbTikTokService(GestioneApplicazioneRepository gestioneApplicazioneRepository) {
        this.gestioneApplicazioneRepository = gestioneApplicazioneRepository;
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

    /**
     * attraverso quuesto motodo, quando scade il token per fare le operazioni su tiktok, se viene passato il refresh_token,
     * genera un nuovo token senza chiedere l'autenticazione, che può essere usato per fare le operazioni come il precedente.
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

    //---------------------------------------------------------------------------------

    public void saveToken_e_refreshToke(String json){
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


    public void refreshToken(){
        try{
            logger.info("sono in refreshToken");
            GestioneApplicazione tokenRefreshTiktok = gestioneApplicazioneRepository.findByName("TOKEN_REFRESH_TIKTOK");
            String json = refreshAccessToken( tokenRefreshTiktok.getValueString() );
            logger.info("json refreshAccessToken:"+ json);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(json);
            String newAccessToken = jsonNode.get("access_token").asText();

            // Recupera l'entità con name uguale a "CSRF_TIKTOK" dal database
            GestioneApplicazione tokenTiktok = gestioneApplicazioneRepository.findByName("TOKEN_TIKTOK");
            if (tokenTiktok != null) {
                tokenTiktok.setValueString( newAccessToken );
                gestioneApplicazioneRepository.save(tokenTiktok);
                logger.info("Value newAccessToken updated in the database!");
            } else {
                logger.info("Record with name 'TOKEN_TIKTOK' not found in the database.");
            }

        } catch (Exception e) {
            logger.info("Error updating value in the database: " + e.getMessage());
            e.printStackTrace();
        }
    }






}
