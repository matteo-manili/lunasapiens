package com.lunasapiens.entity;

import com.lunasapiens.utils.Utils;
import com.lunasapiens.config.PostgreSQLVectorJdbcType;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcType;

import java.io.Serializable;
import java.time.LocalDateTime;



@Entity
public class ArticleContent implements Serializable {

    public ArticleContent() {
        // Hibernate ha bisogno di un costruttore vuoto
    }

    public ArticleContent(Long id, String content, LocalDateTime createdAt,
                          String title, String seoUrl, String metaDescription) {
        this.id = id;
        this.content = content;
        this.createdAt = createdAt;
        this.title = title;
        this.seoUrl = seoUrl;
        this.metaDescription = metaDescription;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;


    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;



    // Campo nuovo: Titolo SEO
    @Column(unique = true, length = 200, nullable = false)
    private String title;

    // Campo nuovo: URL parlante SEO-friendly
    @Column(name = "seo_url", unique = true, length = 200, nullable = false)
    private String seoUrl;

    // Campo nuovo: Meta description (può rimanere nullable)
    @Column(name = "meta_description", columnDefinition = "TEXT")
    private String metaDescription;



    // Usiamo un tipo JDBC custom (PostgreSQLVectorJdbcType) perché Hibernate non supporta nativamente
    // il tipo 'vector' di PostgreSQL (pgvector). Questo JdbcType gestisce la conversione tra
    // Float[] in Java e il formato accettato da pgvector, permettendo di salvare e leggere correttamente
    // gli embeddings direttamente dal database.

    // Embedding vettoriale (tipo pgvector) 384 - 1024
    @Column(columnDefinition = "vector(1024)")
    @JdbcType(PostgreSQLVectorJdbcType.class)
    private Float[] embedding;



    @PrePersist
    protected void prePersist() {
        this.createdAt = Utils.getNowRomeEurope().toLocalDateTime(); // Imposta la data attuale quando l'articolo viene salvato per la prima volta

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

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getSeoUrl() { return seoUrl; }

    public void setSeoUrl(String seoUrl) { this.seoUrl = seoUrl; }

    public String getMetaDescription() { return metaDescription; }

    public void setMetaDescription(String metaDescription) { this.metaDescription = metaDescription; }

    public Float[] getEmbedding() {
        return embedding;
    }

    public void setEmbedding(Float[] embedding) {
        this.embedding = embedding;
    }
}
