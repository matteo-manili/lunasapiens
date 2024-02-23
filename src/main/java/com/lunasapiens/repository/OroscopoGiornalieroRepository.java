package com.lunasapiens.repository;


import com.lunasapiens.entity.GestioneApplicazione;
import com.lunasapiens.entity.OroscopoGiornaliero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface OroscopoGiornalieroRepository extends JpaRepository<OroscopoGiornaliero, Long> {
        // Puoi aggiungere eventuali metodi personalizzati se necessario

    OroscopoGiornaliero findByDataOroscopo(Date dataOroscopo);

    Optional<OroscopoGiornaliero> findFirstByOrderByIdDesc();

    @Query("SELECT COUNT(o) > 0 FROM OroscopoGiornaliero o WHERE o.numSegno = :numSegno AND o.dataOroscopo = :dataOroscopo")
    boolean existsByNumSegnoAndDataOroscopo(Integer numSegno, Date dataOroscopo);


    @Query("SELECT o FROM OroscopoGiornaliero o WHERE o.numSegno = :numSegno AND o.dataOroscopo = :dataOroscopo")
    OroscopoGiornaliero findByNumSegnoAndDataOroscopo(@Param("numSegno") Integer numSegno, @Param("dataOroscopo") Date dataOroscopo);

}


