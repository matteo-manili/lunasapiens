package com.lunasapiens.repository;

import com.lunasapiens.entity.ProfiloUtente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfiloUtenteRepository extends JpaRepository<ProfiloUtente, Long>, ProfiloUtenteCustomRepository {


    Optional<ProfiloUtente> findFirstByOrderByIdDesc();

    @Query("SELECT o FROM ProfiloUtente o WHERE o.email = :email")
    Optional<ProfiloUtente> findByEmail(@Param("email") String email);


    @Query("SELECT o FROM ProfiloUtente o WHERE o.confirmationCode = :confirmationCode")
    Optional<ProfiloUtente> findByConfirmationCode(@Param("confirmationCode") String confirmationCode);



}


