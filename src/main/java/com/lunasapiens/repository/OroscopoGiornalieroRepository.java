package com.lunasapiens.repository;

import com.lunasapiens.entity.OroscopoGiornaliero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OroscopoGiornalieroRepository extends JpaRepository<OroscopoGiornaliero, Long> {

    OroscopoGiornaliero findByDataOroscopo(LocalDateTime dataOroscopo);

    Optional<OroscopoGiornaliero> findFirstByOrderByIdDesc();


    @Query("SELECT o FROM OroscopoGiornaliero o WHERE o.numSegno = :numSegno AND o.dataOroscopo = :dataOroscopo")
    OroscopoGiornaliero findByNumSegnoAndDataOroscopo(@Param("numSegno") Integer numSegno, @Param("dataOroscopo") LocalDateTime dataOroscopo);


    @Query("SELECT o FROM OroscopoGiornaliero o WHERE o.nomeFileVideo = :nomeFileVideo")
    Optional<OroscopoGiornaliero> findByNomeFileVideo(@Param("nomeFileVideo") String nomeFileVideo);



    @Query("SELECT new com.lunasapiens.entity.OroscopoGiornaliero(o.id, o.numSegno, o.testoOroscopo, o.dataOroscopo, o.nomeFileVideo) FROM com.lunasapiens.entity.OroscopoGiornaliero o " +
            "WHERE o.dataOroscopo = :dataOroscopo ORDER BY o.numSegno asc")
    List<OroscopoGiornaliero> findAllByDataOroscopoWithoutVideo(@Param("dataOroscopo") LocalDateTime dataOroscopo);
    //List<OroscopoGiornaliero> findAllByDataOroscopoWithoutVideo(@Param("dataOroscopo") Date dataOroscopo);


    @Query("SELECT o FROM OroscopoGiornaliero o WHERE o.dataOroscopo = :dataOroscopo ORDER BY o.numSegno ASC")
    List<OroscopoGiornaliero> findAllByDataOroscopo(@Param("dataOroscopo") LocalDateTime dataOroscopo);


    @Modifying
    @Transactional
    @Query("DELETE FROM OroscopoGiornaliero o WHERE o.dataOroscopo < :currentDate")
    void deleteByDataOroscopoBefore(@Param("currentDate") LocalDateTime currentDate);





}


