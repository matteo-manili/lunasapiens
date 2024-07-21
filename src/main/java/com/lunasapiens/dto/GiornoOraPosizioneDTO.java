package com.lunasapiens.dto;

import com.lunasapiens.Constants;

import java.time.LocalDateTime;

public class GiornoOraPosizioneDTO {

    private int ora;
    private int minuti;
    private int giorno;
    private int mese;
    private int anno;
    private double lon;
    private double lat;

    String giornoMeseAnnoFormattato;

    // Costruttori, getter e setter

    public GiornoOraPosizioneDTO() {
        // Costruttore vuoto necessario per la deserializzazione JSON
    }

    public GiornoOraPosizioneDTO(int ora, int minuti, int giorno, int mese, int anno, double lat, double lon) {
        LocalDateTime dateTime = LocalDateTime.of(anno, mese, giorno, ora, minuti, 0);
        this.ora = ora;
        this.minuti = minuti;
        this.giorno = giorno;
        this.mese = mese;
        this.anno = anno;
        this.lat = lat;
        this.lon = lon;
        this.giornoMeseAnnoFormattato = dateTime.format(Constants.FORMATTER_GIORNO_MESE_ANNO);
    }

    // Getter e setter

    public int getOra() {
        return ora;
    }

    public void setOra(int ora) {
        this.ora = ora;
    }

    public int getMinuti() {
        return minuti;
    }

    public void setMinuti(int minuti) {
        this.minuti = minuti;
    }

    public int getGiorno() {
        return giorno;
    }

    public void setGiorno(int giorno) {
        this.giorno = giorno;
    }

    public int getMese() {
        return mese;
    }

    public void setMese(int mese) {
        this.mese = mese;
    }

    public int getAnno() {
        return anno;
    }

    public void setAnno(int anno) {
        this.anno = anno;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public String getGiornoMeseAnnoFormattato() {
        return giornoMeseAnnoFormattato;
    }

    public void setGiornoMeseAnnoFormattato(String giornoMeseAnnoFormattato) {
        this.giornoMeseAnnoFormattato = giornoMeseAnnoFormattato;
    }


}

