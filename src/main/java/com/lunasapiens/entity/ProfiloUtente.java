package com.lunasapiens.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "profilo_utente")
public class ProfiloUtente implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String password; // NON LO USO

    @Column
    private String ruolo; // NON LO USO

    @Column(name = "data_creazione", nullable = false)
    private LocalDateTime dataCreazione;

    @Column(name = "data_ultimo_accesso")
    private LocalDateTime dataUltimoAccesso;

    @Column(name = "indirizzo_ip", nullable = false)
    private String indirizzoIp;

    @Column(name = "email_oroscopo_girnaliero")
    private boolean emailOroscopoGiornaliero;

    @Column(name = "email_agg_tema_natale")
    private boolean emailAggiornamentiTemaNatale;

    @Column
    private String confirmationCode;

    // Costruttori
    public ProfiloUtente() {}

    public ProfiloUtente(String email, String password, String ruolo, LocalDateTime dataCreazione, LocalDateTime dataUltimoAccesso, String indirizzoIp,
                         boolean emailOroscopoGiornaliero, boolean emailAggiornamentiTemaNatale, String confirmationCode) {
        this.email = email;
        this.password = password;
        this.ruolo = ruolo;
        this.dataCreazione = dataCreazione;
        this.dataUltimoAccesso = dataUltimoAccesso;
        this.indirizzoIp = indirizzoIp;
        this.emailOroscopoGiornaliero = emailOroscopoGiornaliero;
        this.emailAggiornamentiTemaNatale = emailAggiornamentiTemaNatale;
        this.confirmationCode = confirmationCode;
    }



    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRuolo() {
        return ruolo;
    }

    public void setRuolo(String ruolo) {
        this.ruolo = ruolo;
    }

    public LocalDateTime getDataCreazione() {
        return dataCreazione;
    }

    public void setDataCreazione(LocalDateTime dataCreazione) {
        this.dataCreazione = dataCreazione;
    }

    public LocalDateTime getDataUltimoAccesso() {
        return dataUltimoAccesso;
    }

    public void setDataUltimoAccesso(LocalDateTime dataUltimoAccesso) {
        this.dataUltimoAccesso = dataUltimoAccesso;
    }

    public String getIndirizzoIp() {
        return indirizzoIp;
    }

    public void setIndirizzoIp(String indirizzoIp) {
        this.indirizzoIp = indirizzoIp;
    }

    public boolean isEmailOroscopoGiornaliero() {
        return emailOroscopoGiornaliero;
    }

    public void setEmailOroscopoGiornaliero(boolean emailOroscopoGiornaliero) {
        this.emailOroscopoGiornaliero = emailOroscopoGiornaliero;
    }

    public boolean isEmailAggiornamentiTemaNatale() {
        return emailAggiornamentiTemaNatale;
    }

    public void setEmailAggiornamentiTemaNatale(boolean emailAggiornamentiTemaNatale) {
        this.emailAggiornamentiTemaNatale = emailAggiornamentiTemaNatale;
    }

    public String getConfirmationCode() { return confirmationCode; }
    public void setConfirmationCode(String confirmationCode) { this.confirmationCode = confirmationCode; }
}
