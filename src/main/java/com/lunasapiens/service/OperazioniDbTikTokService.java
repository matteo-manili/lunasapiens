package com.lunasapiens.service;

import com.lunasapiens.entity.GestioneApplicazione;
import com.lunasapiens.repository.GestioneApplicazioneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OperazioniDbTikTokService {


    private final GestioneApplicazioneRepository gestioneApplicazioneRepository;

    @Autowired
    public OperazioniDbTikTokService(GestioneApplicazioneRepository gestioneApplicazioneRepository) {
        this.gestioneApplicazioneRepository = gestioneApplicazioneRepository;
    }

    /*
    public void saveValue(String columnName, Object value) {
        GestioneApplicazione entity = new GestioneApplicazione();
        entity.setName(columnName);
        entity.setValueString(value.toString());
        entity.setValueNumber(Long.parseLong(value.toString()));
        entity.setCommento(value.toString()+"_commento");

        gestioneApplicazioneRepository.save(entity);
    }
*/





}
