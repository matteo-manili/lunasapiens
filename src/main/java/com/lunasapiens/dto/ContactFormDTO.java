package com.lunasapiens.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;


public class ContactFormDTO {

    @NotBlank(message = "Il nome è obbligatorio")
    private String name;

    @NotBlank(message = "L'email è obbligatoria")
    @Email(message = "Inserisci un'email valida")
    private String email;

    @NotBlank(message = "Il messaggio è obbligatorio")
    private String message;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
