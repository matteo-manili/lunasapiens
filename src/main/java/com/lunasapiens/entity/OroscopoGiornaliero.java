package com.lunasapiens.entity;



import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Entity
@Table(name = "oroscopo_giornaliero", uniqueConstraints = @UniqueConstraint(columnNames = {"id_segno", "data_oroscopo"}))
public class OroscopoGiornaliero implements Serializable {

    private Long id;

    private Long idSegno;
    private String testoOroscopo;
    private Date dataOroscopo;


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }


    @Column(name = "id_segno")
    public Long getIdSegno() {
        return idSegno;
    }

    public void setIdSegno(Long id_segno) {
        this.idSegno = idSegno;
    }

    @Lob
    @Column(name = "testo_oroscopo", columnDefinition = "TEXT")
    public String getTestoOroscopo() {
        return testoOroscopo;
    }
    public void setTestoOroscopo(String testoOroscopo) {
        this.testoOroscopo = testoOroscopo;
    }


    @Column(name = "data_oroscopo")
    public Date getDataOroscopo() {
        return dataOroscopo;
    }
    public void setDataOroscopo(Date dataOroscopo) {
        this.dataOroscopo = dataOroscopo;
    }


    public OroscopoGiornaliero(Long idSegno, String testoOroscopo, Date dataOroscopo) {
        this.idSegno = idSegno;
        this.testoOroscopo = testoOroscopo;
        this.dataOroscopo = dataOroscopo;
    }
}