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

    EmailUtenti findByData(Date data);

    Optional<EmailUtenti> findFirstByOrderByIdDesc();


    @Query("SELECT o FROM EmailUtenti o WHERE o.email = :email")
    Optional<EmailUtenti> findByEmail(@Param("email") String email);







}


