package com.lunasapiens.entity;

import com.lunasapiens.config.PostgreSQLVectorJdbcType;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcType;

import java.io.Serializable;

@Entity
@Table(
        name = "chunks",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"videoId", "chunkIndex"})
        }
)
public class Chunks implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ID del video da cui proviene il chunk
    @Column(nullable = false)
    private Long videoId;

    // Indice del chunk all'interno del video
    @Column(nullable = false)
    private Integer chunkIndex;

    // Testo del chunk
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    // Embedding vettoriale (tipo pgvector)
    @Column(columnDefinition = "vector(384)")
    @JdbcType(PostgreSQLVectorJdbcType.class)
    private Float[] embedding;



    // ----- Getters e Setters -----

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public Integer getChunkIndex() {
        return chunkIndex;
    }

    public void setChunkIndex(Integer chunkIndex) {
        this.chunkIndex = chunkIndex;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Float[] getEmbedding() {
        return embedding;
    }

    public void setEmbedding(Float[] embedding) {
        this.embedding = embedding;
    }
}
