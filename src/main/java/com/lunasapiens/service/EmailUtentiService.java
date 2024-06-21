package com.lunasapiens.service;


import com.lunasapiens.entity.EmailUtenti;
import com.lunasapiens.repository.EmailUtentiRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class EmailUtentiService {

    private static final Logger logger = LoggerFactory.getLogger(EmailUtentiService.class);

    private final EmailUtentiRepository emailUtentiRepository;

    @Autowired
    public EmailUtentiService(EmailUtentiRepository emailUtentiRepository) {
        this.emailUtentiRepository = emailUtentiRepository;
    }

    public List<EmailUtenti> findAll() {
        return emailUtentiRepository.findAll();
    }


    // Metodo per recuperare l'ultimo record inserito
    @Transactional(readOnly = true) // Disabilita l'autocommit
    public Optional<EmailUtenti> getUltimoRecordInserito() {
        return emailUtentiRepository.findFirstByOrderByIdDesc();
    }


    @Transactional(readOnly = true)
    public Optional<EmailUtenti> findByEmailUtenti(String email) {
        return emailUtentiRepository.findByEmail(email);
    }



    @Transactional
    public EmailUtenti salvaEmailUtenti(String email, Date data, boolean subscription) throws Exception {
        EmailUtenti emailUtenti = new EmailUtenti(email, data, subscription);
        // Salvare l'oggetto nel database utilizzando il repository
        return emailUtentiRepository.save(emailUtenti);
    }









}
