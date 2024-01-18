package com.lunasapiens.dto;

import java.util.Date;

public class OroscopoGiornalieroDTO {

    private Long id;
    private Long idSegno;
    private String testoOroscopo;
    private Date dataOroscopo;

    public OroscopoGiornalieroDTO(Long id, Long idSegno, String testoOroscopo, Date dataOroscopo) {
        this.id = id;
        this.idSegno = idSegno;
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

    public Long getIdSegno() {
        return idSegno;
    }

    public void setIdSegno(Long idSegno) {
        this.idSegno = idSegno;
    }

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

