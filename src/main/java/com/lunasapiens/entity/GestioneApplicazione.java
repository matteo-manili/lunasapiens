package com.lunasapiens.entity;



import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "data_gestione_applicazione")
public class GestioneApplicazione implements Serializable {

    private Long id;
    private String name;
    private String valueString;
    private Long valueNumber;
    private String commento;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    @Column(unique = true)
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "value_number")
    public Long getValueNumber() {
        return valueNumber;
    }
    public void setValueNumber(Long valueNumber) {
        this.valueNumber = valueNumber;
    }

    @Column(name = "value_string")
    public String getValueString() {
        return valueString;
    }
    public void setValueString(String valueString) {
        this.valueString = valueString;
    }

    @Column(name = "commento")
    public String getCommento() {
        return commento;
    }
    public void setCommento(String commento) {
        this.commento = commento;
    }

    /**
     * Default constructor - creates a new instance with no values set.
     */
    public GestioneApplicazione() {
    }
    public GestioneApplicazione(final String name) {
        this.name = name;
    }


}