package com.lunasapiens.dto;

public class RelationshipOption {

    private String code;
    private String description;

    public RelationshipOption(String code, String description) {
        this.code = code;
        this.description = description;
    }

    // Getters e Setters
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description; // Ritorna la descrizione per la visualizzazione
    }
}

