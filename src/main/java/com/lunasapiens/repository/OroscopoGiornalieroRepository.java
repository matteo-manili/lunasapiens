package com.lunasapiens.repository;


import com.lunasapiens.entity.GestioneApplicazione;
import com.lunasapiens.entity.OroscopoGiornaliero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface OroscopoGiornalieroRepository extends JpaRepository<OroscopoGiornaliero, Long> {
        // Puoi aggiungere eventuali metodi personalizzati se necessario

    OroscopoGiornaliero findByDataOroscopo(Date dataOroscopo);







}


