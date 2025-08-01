package com.lunasapiens.service;


import com.lunasapiens.entity.ProfiloUtente;
import com.lunasapiens.repository.ProfiloUtenteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProfiloUtenteService {

    private static final Logger logger = LoggerFactory.getLogger(ProfiloUtenteService.class);

    private final ProfiloUtenteRepository profiloUtenteRepository;

    @Autowired
    public ProfiloUtenteService(ProfiloUtenteRepository profiloUtenteRepository) {
        this.profiloUtenteRepository = profiloUtenteRepository;
    }

    public List<ProfiloUtente> findAll() {
        return profiloUtenteRepository.findAll();
    }


    @Transactional(readOnly = true)
    public List<ProfiloUtente> getUtentiConOroscopoAttivo() {
        return profiloUtenteRepository.findByEmailOroscopoGiornalieroTrue();
    }


    // Metodo per recuperare l'ultimo record inserito
    @Transactional(readOnly = true) // Disabilita l'autocommit
    public Optional<ProfiloUtente> getUltimoRecordInserito() {
        return profiloUtenteRepository.findFirstByOrderByIdDesc();
    }


    @Transactional(readOnly = true)
    public Optional<ProfiloUtente> findByProfiloUtente(String email) {
        return profiloUtenteRepository.findByEmail(email);
    }


    @Transactional
    public ProfiloUtente salvaProfiloUtente(String email, String password, String ruolo, LocalDateTime dataCreazione, LocalDateTime dataUltimoAccesso,
                                            String indirizzoIp, boolean emailOroscopoGiornaliero, boolean emailAggiornamentiTemaNatale, String confirmationCode) throws Exception {

        ProfiloUtente profiloUtente = new ProfiloUtente(email, password, ruolo, dataCreazione, dataUltimoAccesso, indirizzoIp,
                emailOroscopoGiornaliero, emailAggiornamentiTemaNatale, confirmationCode);
        profiloUtenteRepository.updateSequence();
        return profiloUtenteRepository.save(profiloUtente);
    }







}
