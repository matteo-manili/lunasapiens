package com.lunasapiens.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Entity
@Table(name = "email_utenti")
public class EmailUtenti implements Serializable {

    private Long id;
    private String email;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dataRegistrazione;
    private boolean subscription;
    private String confirmationCode;


    /**
     * GenerationType.AUTO
     * Classe persistente per la gestione delle iscrizioni email degli utenti.
     * Utilizza GenerationType.AUTO per generare automaticamente ID univoci
     * tramite il provider di persistenza, adattandosi alla configurazione del database PostgreSQL.
     * La strategia esatta di generazione degli ID dipende dalla configurazione specifica del provider.
     */

    /**
     * GenerationType.IDENTITY
     * Classe persistente per la gestione delle iscrizioni email degli utenti.
     * Utilizza GenerationType.IDENTITY per generare automaticamente ID univoci e sequenziali
     * tramite una colonna auto-incrementale nel database PostgreSQL.
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    @Column(nullable = false, unique = true)
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    @Column(nullable = false)
    public Date getDataRegistrazione() { return dataRegistrazione; }
    public void setDataRegistrazione(Date dataRegistrazione) { this.dataRegistrazione = dataRegistrazione; }

    @Column(nullable = false)
    public boolean isSubscription() { return subscription; }
    public void setSubscription(boolean subscription) { this.subscription = subscription; }

    @Column
    public String getConfirmationCode() { return confirmationCode; }
    public void setConfirmationCode(String confirmationCode) { this.confirmationCode = confirmationCode; }




    public EmailUtenti(String email, Date dataRegistrazione, boolean subscription, String confirmationCode) {
        this.email = email;
        this.dataRegistrazione = dataRegistrazione;
        this.subscription = subscription;
        this.confirmationCode = confirmationCode;
    }

    public EmailUtenti() { }
}