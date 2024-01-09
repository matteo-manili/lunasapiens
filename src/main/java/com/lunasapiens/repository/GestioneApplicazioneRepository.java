package com.lunasapiens.repository;


import com.lunasapiens.entity.GestioneApplicazione;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GestioneApplicazioneRepository extends JpaRepository<GestioneApplicazione, Long> {
        // Puoi aggiungere eventuali metodi personalizzati se necessario

    GestioneApplicazione findByName(String name);







}


