package com.lunasapiens.entity;

import com.lunasapiens.config.PostgreSQLVectorJdbcType;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcType;

import java.io.Serializable;

@Entity
@Table(
        name = "chunks",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"numero_video_chunks", "chunkIndex"})
        }
)
public class Chunks implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "numero_video_chunks", referencedColumnName = "numero_video")
    private VideoChunks videoChunks;



    // ----- Getters e Setters -----

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public VideoChunks getVideoChunks() { return videoChunks; }

    public void setVideoChunks(VideoChunks videoChunks) { this.videoChunks = videoChunks; }

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
