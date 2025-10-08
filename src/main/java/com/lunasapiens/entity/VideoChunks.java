package com.lunasapiens.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "video_chunks")
public class VideoChunks implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Chiave primaria, collegata a chunks.videoId

    @Column(nullable = false, length = 255)
    private String title; // Titolo del video

    @Column(columnDefinition = "TEXT", nullable = false)
    private String fullContent; // Testo lungo (fino a 10.000+ parole)


    /**
     * bisogna inserire per forza un valore json, per il null va bene così: {}
     */
    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> metadati;


    // Colonna dedicata per join con Chunks
    @Column(name = "numero_video", nullable = false, unique = true)
    private Long numeroVideo;

    @OneToMany
    @JoinColumn(name = "numero_video_chunks", referencedColumnName = "numero_video")
    private List<Chunks> chunks;



    // ----- Getters & Setters -----

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFullContent() {
        return fullContent;
    }

    public void setFullContent(String fullContent) {
        this.fullContent = fullContent;
    }

    public Long getNumeroVideo() { return numeroVideo; }

    public void setNumeroVideo(Long numeroVideo) { this.numeroVideo = numeroVideo; }


    public Map<String, Object> getMetadati() {
        return metadati;
    }

    public void setMetadati(Map<String, Object> metadati) {
        this.metadati = metadati;
    }

    public List<Chunks> getChunks() { return chunks; }

    public void setChunks(List<Chunks> chunks) { this.chunks = chunks; }
}
