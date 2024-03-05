package com.lunasapiens.repository;


import com.lunasapiens.entity.GestioneApplicazione;
import com.lunasapiens.entity.OroscopoGiornaliero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface OroscopoGiornalieroRepository extends JpaRepository<OroscopoGiornaliero, Long> {

    OroscopoGiornaliero findByDataOroscopo(Date dataOroscopo);

    Optional<OroscopoGiornaliero> findFirstByOrderByIdDesc();

    List<OroscopoGiornaliero> findAllByDataOroscopo(Date dataOroscopo);


    @Query("SELECT COUNT(o) > 0 FROM OroscopoGiornaliero o WHERE o.video IS NOT NULL AND o.numSegno = :numSegno AND o.dataOroscopo = :dataOroscopo")
    boolean existsByNumSegnoAndDataOroscopo(Integer numSegno, Date dataOroscopo);


    @Query("SELECT o FROM OroscopoGiornaliero o WHERE o.numSegno = :numSegno AND o.dataOroscopo = :dataOroscopo")
    OroscopoGiornaliero findByNumSegnoAndDataOroscopo(@Param("numSegno") Integer numSegno, @Param("dataOroscopo") Date dataOroscopo);


    @Query("SELECT o FROM OroscopoGiornaliero o WHERE o.nomeFileVideo = :nomeFileVideo")
    Optional<OroscopoGiornaliero> findByNomeFileVideo(@Param("nomeFileVideo") String nomeFileVideo);


    @Query("SELECT new com.lunasapiens.entity.OroscopoGiornaliero(o.id, o.numSegno, o.testoOroscopo, o.dataOroscopo, o.nomeFileVideo) FROM OroscopoGiornaliero o WHERE o.dataOroscopo = :dataOroscopo")
    List<OroscopoGiornaliero> findAllByDataOroscopoWithoutVideo(@Param("dataOroscopo") Date dataOroscopo);




}


