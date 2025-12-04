package com.lunasapiens.service;

import com.lunasapiens.utils.Utils;
import com.lunasapiens.dto.GiornoOraPosizioneDTO;
import com.lunasapiens.entity.OroscopoGiornaliero;
import com.lunasapiens.repository.OroscopoGiornalieroRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import java.util.List;
import java.util.Optional;

@Service
public class OroscopoGiornalieroService {

    private static final Logger logger = LoggerFactory.getLogger(OroscopoGiornalieroService.class);

    private final OroscopoGiornalieroRepository oroscopoGiornalieroRepository;

    @Autowired
    public OroscopoGiornalieroService(OroscopoGiornalieroRepository oroscopoGiornalieroRepository) {
        this.oroscopoGiornalieroRepository = oroscopoGiornalieroRepository;
    }


    @Transactional(readOnly = true)
    public OroscopoGiornaliero findByNumSegnoAndDataOroscopo(Integer numSegno, LocalDateTime dataOroscopo) {
        return oroscopoGiornalieroRepository.findByNumSegnoAndDataOroscopo(numSegno, dataOroscopo);
    }

    @Transactional(readOnly = true)
    public Optional<OroscopoGiornaliero> findByNomeFileVideo(String nomeFileVideo) {
        return oroscopoGiornalieroRepository.findByNomeFileVideo(nomeFileVideo);
    }

    @Transactional(readOnly = true)
    public List<OroscopoGiornaliero> findAllByDataOroscopo(LocalDateTime dataOroscopo) {
        return oroscopoGiornalieroRepository.findAllByDataOroscopo(dataOroscopo);
    }

    // Metodo per recuperare l'oroscopo attraverso l'ID
    public Optional<OroscopoGiornaliero> getOroscopoById(Long id) {
        return oroscopoGiornalieroRepository.findById(id);
    }


    // Metodo per recuperare l'ultimo record inserito
    @Transactional(readOnly = true) // Disabilita l'autocommit
    public Optional<OroscopoGiornaliero> getUltimoRecordInserito() {
        return oroscopoGiornalieroRepository.findFirstByOrderByIdDesc();
    }


    @Transactional(readOnly = true)
    public List<OroscopoGiornaliero> findAllByDataOroscopoWithoutVideo(LocalDateTime dataOroscopo) {
        return oroscopoGiornalieroRepository.findAllByDataOroscopoWithoutVideo(dataOroscopo);
    }




    @Transactional
    public OroscopoGiornaliero salvaOroscoopoGiornaliero(int segnoNumero, StringBuilder sB, GiornoOraPosizioneDTO giornoOraPosizioneDTO,
                                                         byte[] video, String nomeFileVideo) throws Exception {

        // Ottenere l'oggetto Date dal Calendar
        LocalDateTime date = Utils.convertiGiornoOraPosizioneDTOInLocalDateTime(giornoOraPosizioneDTO);

        OroscopoGiornaliero oroscopoGiornaliero = new OroscopoGiornaliero(segnoNumero, sB.toString(), date, video, nomeFileVideo);

        // Salvare l'oggetto nel database utilizzando il repository
        return oroscopoGiornalieroRepository.save(oroscopoGiornaliero);

    }


    public List<OroscopoGiornaliero> findAll() {
        return oroscopoGiornalieroRepository.findAll();
    }







}
