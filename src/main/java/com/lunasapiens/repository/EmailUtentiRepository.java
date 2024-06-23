package com.lunasapiens.repository;

import com.lunasapiens.entity.EmailUtenti;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmailUtentiRepository extends JpaRepository<EmailUtenti, Long> {


    Optional<EmailUtenti> findFirstByOrderByIdDesc();

    @Query("SELECT o FROM EmailUtenti o WHERE o.email = :email")
    Optional<EmailUtenti> findByEmail(@Param("email") String email);


    @Query("SELECT o FROM EmailUtenti o WHERE o.confirmationCode = :confirmationCode")
    Optional<EmailUtenti> findByConfirmationCode(@Param("confirmationCode") String confirmationCode);


    //@Query(value = "SELECT * FROM email_utenti WHERE confirmation_code = :confirmation_code", nativeQuery = true)
    //Optional<EmailUtenti> findByConfirmationCode(@Param("confirmation_code") String confirmation_code);





}


