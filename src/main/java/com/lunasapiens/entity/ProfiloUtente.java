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

    @Column(nullable = false)
    private String password; // Nuova colonna per la password

    @Column(nullable = false)
    private String ruolo; // L'applicazione prevede solo un ruolo per utente

    @Column(name = "data_creazione", nullable = false)
    private LocalDateTime dataCreazione;

    @Column(name = "data_ultimo_accesso")
    private LocalDateTime dataUltimoAccesso;

    @Column(name = "indirizzo_ip")
    private String indirizzoIp;

    @Column(name = "token_jwt_oauth")
    private String tokenJwtOauth;

    // Costruttori
    public ProfiloUtente() {}

    public ProfiloUtente(String email, String password, String ruolo, LocalDateTime dataCreazione, String indirizzoIp, String tokenJwtOauth) {
        this.email = email;
        this.password = password;
        this.ruolo = ruolo;
        this.dataCreazione = dataCreazione;
        this.indirizzoIp = indirizzoIp;
        this.tokenJwtOauth = tokenJwtOauth;
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

    public String getTokenJwtOauth() {
        return tokenJwtOauth;
    }

    public void setTokenJwtOauth(String tokenJwtOauth) {
        this.tokenJwtOauth = tokenJwtOauth;
    }
}
