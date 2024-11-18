package com.lunasapiens.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.List;

@Entity
public class Article implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    private String content; // Per memorizzare il contenuto dell'editor


    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Image> images; // Associazione con le immagini



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

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }
}
