package com.lunasapiens.entity;

import com.lunasapiens.config.PostgreSQLVectorJdbcType;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcType;

import java.io.Serializable;
import java.time.LocalDateTime;



@Entity
public class ArticleContent implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;


    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;


    // Usiamo un tipo JDBC custom (PostgreSQLVectorJdbcType) perché Hibernate non supporta nativamente
    // il tipo 'vector' di PostgreSQL (pgvector). Questo JdbcType gestisce la conversione tra
    // Float[] in Java e il formato accettato da pgvector, permettendo di salvare e leggere correttamente
    // gli embeddings direttamente dal database.

    //@Column(columnDefinition = "vector(768)")
    @Column(columnDefinition = "vector(384)")
    @JdbcType(PostgreSQLVectorJdbcType.class)
    private Float[] embedding;



    @PrePersist
    protected void prePersist() {
        this.createdAt = LocalDateTime.now(); // Imposta la data attuale quando l'articolo viene salvato per la prima volta
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Float[] getEmbedding() {
        return embedding;
    }

    public void setEmbedding(Float[] embedding) {
        this.embedding = embedding;
    }
}
