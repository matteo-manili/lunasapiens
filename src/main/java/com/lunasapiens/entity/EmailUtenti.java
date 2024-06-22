package com.lunasapiens.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Entity
@Table(name = "email_utenti")
public class EmailUtenti implements Serializable {

    private Long id;
    private String email;
    private Date data;
    private boolean subscription;


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

    @Column(name = "data", nullable = false)
    public Date getData() {
        return data;
    }
    public void setData(Date dataOroscopo) {
        this.data = data;
    }

    @Column(nullable = false)
    public boolean isSubscription() { return subscription; }
    public void setSubscription(boolean subscription) { this.subscription = subscription; }



    public EmailUtenti(String email, Date data, boolean subscription) {
        this.email = email;
        this.data = data;
        this.subscription = subscription;
    }


}