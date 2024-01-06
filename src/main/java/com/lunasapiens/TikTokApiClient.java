package com.lunasapiens;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;


public class TikTokApiClient {



        public static void main(String[] args) throws TelegramApiRequestException {
            // Configurazione
            String clientKey = "awk0myrx70nr9gkr";
            String clientSecret = "9dDg7Kgg2lXK8JoO1THhOl5kbDz8tuxL";
            String redirectUri = "https://www.lunasapiens.com/tiktok-outh/";
            String scope = "user.info.basic";
            String authorizationUrl = "https://www.tiktok.com/v2/auth/authorize/";

            // Creazione del CSRF state token
            String csrfState = generateCSRFState();

            // Costruzione dell'URL di autorizzazione
            String authorizationUri = UriComponentsBuilder.fromHttpUrl(authorizationUrl)
                    .queryParam("client_key", clientKey)
                    .queryParam("scope", scope)
                    .queryParam("response_type", "code")
                    .queryParam("redirect_uri", redirectUri)
                    .queryParam("state", csrfState)
                    .build().toUriString();

            TelegramBot telegramBot = new TelegramBot();
            telegramBot.inviaMessaggio("link autenticazione tiktok: "+authorizationUri);

            // Apertura dell'URL nel browser o integrazione con il tuo frontend
            System.out.println("Apri l'URL nel browser: " + authorizationUri);

            // Ora puoi gestire la risposta dell'utente dopo il login e ottenere l'access token
            // ...

        }

        private static String generateCSRFState() {
            // Implementa la logica per generare un CSRF state token univoco
            // Puoi utilizzare un generatore casuale o la logica desiderata
            return "csrf_token";
        }
    }
