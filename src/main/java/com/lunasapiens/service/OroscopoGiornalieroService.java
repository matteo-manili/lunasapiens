package com.lunasapiens.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lunasapiens.dto.GiornoOraPosizioneDTO;
import com.lunasapiens.entity.GestioneApplicazione;
import com.lunasapiens.entity.OroscopoGiornaliero;
import com.lunasapiens.repository.GestioneApplicazioneRepository;
import com.lunasapiens.repository.OroscopoGiornalieroRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

@Service
public class OroscopoGiornalieroService {

    private static final Logger logger = LoggerFactory.getLogger(OroscopoGiornalieroService.class);

    private final OroscopoGiornalieroRepository oroscopoGiornalieroRepository;

    @Autowired
    public OroscopoGiornalieroService(OroscopoGiornalieroRepository oroscopoGiornalieroRepository) {
        this.oroscopoGiornalieroRepository = oroscopoGiornalieroRepository;
    }



    //---------------------------------------------------------------------------------

    public OroscopoGiornaliero salvaOroscoopoGiornaliero(int segnoNumero, StringBuilder sB, GiornoOraPosizioneDTO giornoOraPosizioneDTO){

        try {

            // Creare un oggetto Calendar e impostare i valori
            Calendar calendar = Calendar.getInstance();
            calendar.set(giornoOraPosizioneDTO.getAnno(), giornoOraPosizioneDTO.getMese(), giornoOraPosizioneDTO.getGiorno(), giornoOraPosizioneDTO.getOra(),
                    giornoOraPosizioneDTO.getMinuti()); // I secondi sono impostati a 0

            // Impostare i millisecondi e secondi a 0
            calendar.set(Calendar.SECOND, 0); calendar.set(Calendar.MILLISECOND, 0);

            // Ottenere l'oggetto Date dal Calendar
            Date date = calendar.getTime();

            OroscopoGiornaliero oroscopoGiornaliero = new OroscopoGiornaliero((long)segnoNumero, sB.toString(), date);

            // Salvare l'oggetto nel database utilizzando il repository
            return oroscopoGiornalieroRepository.save(oroscopoGiornaliero);


        } catch (DataIntegrityViolationException e) {
            logger.info("Error DataIntegrityViolationException in the database: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            logger.info("Error updating value in the database: " + e.getMessage());
            e.printStackTrace();
        }

        return null;

    }









}
