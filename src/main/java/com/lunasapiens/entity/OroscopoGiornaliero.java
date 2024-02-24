package com.lunasapiens.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Entity
@Table(name = "oroscopo_giornaliero", uniqueConstraints = @UniqueConstraint(columnNames = {"num_segno", "data_oroscopo"}))
public class OroscopoGiornaliero implements Serializable {

    private Long id;

    private Integer numSegno;
    private String testoOroscopo;
    private Date dataOroscopo;
    private byte[] video;
    private String nomeFileVideo;


    public OroscopoGiornaliero() {
        // Costruttore vuoto richiesto da Hibernate
    }


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }


    @Column(name = "num_segno", nullable = false)
    public Integer getNumSegno() {
        return numSegno;
    }
    public void setNumSegno(Integer numSegno) {
        this.numSegno = numSegno;
    }

    @Lob
    @Column(name = "testo_oroscopo", columnDefinition = "TEXT", nullable = false)
    public String getTestoOroscopo() {
        return testoOroscopo;
    }
    public void setTestoOroscopo(String testoOroscopo) {
        this.testoOroscopo = testoOroscopo;
    }

    @Column(name = "data_oroscopo", nullable = false)
    public Date getDataOroscopo() {
        return dataOroscopo;
    }
    public void setDataOroscopo(Date dataOroscopo) {
        this.dataOroscopo = dataOroscopo;
    }

    @Lob
    @Column(name = "video", nullable = true)
    public byte[] getVideo() {
        return video;
    }
    public void setVideo(byte[] video) {
        this.video = video;
    }

    @Column(name = "nome_file_video")
    public String getNomeFileVideo() {
        return nomeFileVideo;
    }
    public void setNomeFileVideo(String nomeFileVideo) {
        this.nomeFileVideo = nomeFileVideo;
    }




    public OroscopoGiornaliero(Integer numSegno, String testoOroscopo, Date dataOroscopo, byte[] video, String nomeFileVideo) {
        this.numSegno = numSegno;
        this.testoOroscopo = testoOroscopo;
        this.dataOroscopo = dataOroscopo;
        this.video = video;
        this.nomeFileVideo = nomeFileVideo;
    }
}