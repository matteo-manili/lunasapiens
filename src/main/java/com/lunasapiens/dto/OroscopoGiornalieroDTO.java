package com.lunasapiens.dto;

import java.util.Date;

public class OroscopoGiornalieroDTO {

    private Long id;
    private Integer numSegno;
    private String testoOroscopo;
    private Date dataOroscopo;

    public OroscopoGiornalieroDTO(Long id, Integer numSegno, String testoOroscopo, Date dataOroscopo) {
        this.id = id;
        this.numSegno = numSegno;
        this.testoOroscopo = testoOroscopo;
        this.dataOroscopo = dataOroscopo;
    }

    public OroscopoGiornalieroDTO() {
        // Costruttore vuoto necessario per la deserializzazione JSON
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumSegno() { return numSegno; }

    public void setNumSegno(Integer numSegno) { this.numSegno = numSegno; }

    public String getTestoOroscopo() {
        return testoOroscopo;
    }

    public void setTestoOroscopo(String testoOroscopo) {
        this.testoOroscopo = testoOroscopo;
    }

    public Date getDataOroscopo() {
        return dataOroscopo;
    }

    public void setDataOroscopo(Date dataOroscopo) {
        this.dataOroscopo = dataOroscopo;
    }
}

