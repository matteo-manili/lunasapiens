package com.lunasapiens.entity;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
public class ArticleImage implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename;

    /**
     * Stai utilizzando Postgres. In questo caso ci sono due opzioni per memorizzare un array di byte nel database.
     * Uno è il tipo OID, che è un tipo composito in cui il contenuto è archiviato in una tabella separata e la colonna originale contiene solo un ID intero che punta ad essa. Può occupare fino a 4 TB di dati (le versioni più vecchie fino a 2 GB).
     * L'altro è il tipo BYTEA che è esattamente quello che volevi usare. Può occupare fino a 1 GB di dati ed è più che sufficiente per un'immagine del profilo.
     * Stai utilizzando Hibernate 6. Mapperà il tipo di array di byte del campo entità al tipo BYTEA del database Postgres senza problemi. Non utilizzare l'annotazione @Lob perché causerebbe a Hibernate di utilizzare il tipo OID che deve essere letto come un flusso.
     * Soluzione:
     * Rimuovere l'annotazione @Lob dall'entità.
     * Disclaimer: la soluzione è solo per il database Postgres. Altri driver JDBC possono gestirla in modo diverso.
     * https://stackoverflow.com/questions/75985717/unable-to-use-spring-data-jpa-to-extract-users-that-have-a-byte-of-a-profile-i
     */
    @Column(columnDefinition = "bytea") // Aggiungi questa riga per forzare Hibernate a usare BYTEA
    private byte[] data; // Contenuto binario dell'immagine



    private String contentType; // Tipo MIME dell'immagine (es., "image/png" o "image/jpeg")



    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }


}
