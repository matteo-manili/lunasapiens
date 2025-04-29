package com.lunasapiens.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

public class ProfiloUtenteCustomRepositoryImpl implements ProfiloUtenteCustomRepository {

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public void updateSequence() {
        entityManager.createNativeQuery("SELECT setval('profilo_utente_id_seq', (SELECT MAX(id) FROM profilo_utente), true)")
                .getSingleResult();
    }


}
