package com.lunasapiens;


import jakarta.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

@Component
public class TikTokApiClient {



    private TelegramBot telegramBot;
    private ServletContext servletContext;

    @Autowired
    public TikTokApiClient(TelegramBot telegramBot, ServletContext servletContext) {
        this.telegramBot = telegramBot;
        this.servletContext = servletContext;
    }

        public static void main(String[] args) throws TelegramApiRequestException {

            // Crea un'applicazione Spring e ottieni un'istanza di TikTokApiClient dal contesto di Spring
            ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
            TikTokApiClient tikTokApiClient = context.getBean(TikTokApiClient.class);

            // Chiamare il metodo non statico su quell'istanza
            tikTokApiClient.doAutenticazioneTikTok();
        }


        public void doAutenticazioneTikTok(){
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

            //TelegramBot telegramBot = new TelegramBot();
            telegramBot.inviaMessaggio("link autenticazione tiktok: "+authorizationUri);

            // Apertura dell'URL nel browser o integrazione con il tuo frontend
            System.out.println("Apri l'URL nel browser: " + authorizationUri);

            saveCSRFState(csrfState);
            // Ora puoi gestire la risposta dell'utente dopo il login e ottenere l'access token
            // ...
        }


        private static String generateCSRFState() {
            // Implementa la logica per generare un CSRF state token univoco
            // Puoi utilizzare un generatore casuale o la logica desiderata
            return Integer.toString((int) (Math.random() * 1000000)); // Esempio di generazione di un numero casuale come token
            //return "csrf_token";
        }


        private void saveCSRFState(String csrfState) {
            // Salva lo stato CSRF nelle variabili globali di Spring (ServletContext)
            System.out.println("salvo la csrfState nel context: " + csrfState);
            servletContext.setAttribute("csrfStateTikTok", csrfState);
        }



    }
