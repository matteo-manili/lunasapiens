package com.lunasapiens.service;

import com.lunasapiens.config.GoogleRecaptchaConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class RecaptchaVerificationService {


    @Autowired
    private GoogleRecaptchaConfig getRecaptchaKeys;

    private static final String RECAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    public boolean verifyRecaptcha(String token) {
        RestTemplate restTemplate = new RestTemplate();

        // Crea l'URL della richiesta di verifica
        String url = UriComponentsBuilder.fromHttpUrl(RECAPTCHA_VERIFY_URL)
                .queryParam("secret", getRecaptchaKeys.getSecretKey())
                .queryParam("response", token)
                .toUriString();

        // Fai una richiesta POST per verificare il token
        RecaptchaResponse response = restTemplate.postForObject(url, null, RecaptchaResponse.class);

        // Verifica se il token è valido (ad esempio se il punteggio è maggiore di una certa soglia)
        return response.isSuccess() && response.getScore() >= 0.5;  // Soglia di sicurezza (ad esempio 0.5)
    }
}

class RecaptchaResponse {
    private boolean success;
    private float score;
    private String action;

    // getters and setters

    public boolean isSuccess() {
        return success;
    }

    public float getScore() {
        return score;
    }
}

