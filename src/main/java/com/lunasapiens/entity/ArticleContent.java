package com.lunasapiens.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

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


    /* @Column(columnDefinition = "vector(3)")
    @JdbcTypeCode(SqlTypes.OTHER) // gestisce float[] come tipo 'vector' in PostgreSQL
    private Float[] embedding; */
    //@Convert(converter = FloatArrayToVectorConverter.class)


    @Column(columnDefinition = "vector(768)")
    private float[] embedding;



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


    public float[] getEmbedding() {
        return embedding;
    }

    public void setEmbedding(float[] embedding) {
        this.embedding = embedding;
    }
}
