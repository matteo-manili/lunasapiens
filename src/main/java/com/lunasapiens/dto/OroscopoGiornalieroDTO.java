package com.lunasapiens.dto;

import com.lunasapiens.Constants;
import com.lunasapiens.entity.OroscopoGiornaliero;
import java.util.Date;

public class OroscopoGiornalieroDTO {

    private Long id;
    private Integer numSegno;
    private String testoOroscopo;
    private Date dataOroscopo;
    private String nomeFileVideo;

    String nomeSegnoZodiacale;
    String testoOroscopoRifinito;

    public OroscopoGiornalieroDTO() {
    }


    public OroscopoGiornalieroDTO(OroscopoGiornaliero oroscopoGiornaliero) {
        this.id = oroscopoGiornaliero.getId();
        this.numSegno = oroscopoGiornaliero.getNumSegno();
        this.testoOroscopo = oroscopoGiornaliero.getTestoOroscopo();
        this.dataOroscopo = oroscopoGiornaliero.getDataOroscopo();
        this.nomeFileVideo = oroscopoGiornaliero.getNomeFileVideo();
        this.nomeSegnoZodiacale = Constants.segnoZodiacale(oroscopoGiornaliero.getNumSegno());

        String testoOroscopoRifinito = oroscopoGiornaliero.getTestoOroscopo().replace("\n", "<br>");
        //Questa espressione regolare viene utilizzata per rimuovere tutti i tag <br> che si trovano all'inizio della stringa,
        // inclusi eventuali spazi che potrebbero precederli.
        testoOroscopoRifinito = testoOroscopoRifinito.replaceFirst("^\\s*(<br>)+", "");
        this.testoOroscopoRifinito = testoOroscopoRifinito;;
    }




    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumSegno() {
        return numSegno;
    }

    public void setNumSegno(Integer numSegno) {
        this.numSegno = numSegno;
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

    public String getNomeFileVideo() {
        return nomeFileVideo;
    }

    public void setNomeFileVideo(String nomeFileVideo) {
        this.nomeFileVideo = nomeFileVideo;
    }

    public String getNomeSegnoZodiacale() {
        return nomeSegnoZodiacale;
    }

    public void setNomeSegnoZodiacale(String nomeSegnoZodiacale) {
        this.nomeSegnoZodiacale = nomeSegnoZodiacale;
    }

    public String getTestoOroscopoRifinito() {
        return testoOroscopoRifinito;
    }

    public void setTestoOroscopoRifinito(String testoOroscopoRifinito) {
        this.testoOroscopoRifinito = testoOroscopoRifinito;
    }
}
