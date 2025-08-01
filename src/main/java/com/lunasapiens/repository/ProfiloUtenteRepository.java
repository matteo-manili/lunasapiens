package com.lunasapiens.repository;

import com.lunasapiens.entity.ProfiloUtente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfiloUtenteRepository extends JpaRepository<ProfiloUtente, Long>, ProfiloUtenteCustomRepository {

    /**
     * Derived query (stile Spring Data): recupera il primo ProfiloUtente
     * ordinato per id in ordine decrescente (cioè l'ultimo inserito).
     * Genera automaticamente la query senza bisogno di @Query.
     */
    Optional<ProfiloUtente> findFirstByOrderByIdDesc();


    @Query("SELECT o FROM ProfiloUtente o WHERE o.email = :email")
    Optional<ProfiloUtente> findByEmail(@Param("email") String email);


    @Query("SELECT o FROM ProfiloUtente o WHERE o.confirmationCode = :confirmationCode")
    Optional<ProfiloUtente> findByConfirmationCode(@Param("confirmationCode") String confirmationCode);


    /**
     * Derived query (stile Spring Data): sfrutta la naming convention di Spring Data JPA
     * per generare automaticamente una SELECT che restituisce tutti i profili
     * con emailOroscopoGiornaliero = true, senza bisogno di @Query.
     */
    List<ProfiloUtente> findByEmailOroscopoGiornalieroTrue();



}


