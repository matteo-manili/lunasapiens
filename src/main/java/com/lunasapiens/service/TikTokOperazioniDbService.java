package com.lunasapiens.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lunasapiens.entity.GestioneApplicazione;
import com.lunasapiens.repository.GestioneApplicazioneRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TikTokOperazioniDbService {

    private static final Logger logger = LoggerFactory.getLogger(TikTokOperazioniDbService.class);

    private final GestioneApplicazioneRepository gestioneApplicazioneRepository;

    @Autowired
    public TikTokOperazioniDbService(GestioneApplicazioneRepository gestioneApplicazioneRepository) {
        this.gestioneApplicazioneRepository = gestioneApplicazioneRepository;
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









}
