package com.lunasapiens.service;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lunasapiens.config.GoogleRecaptchaConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Nome Progetto reCaptcha v3 lunasapiens.com
 *
 * Andare su https://www.google.com/recaptcha/about/
 * Andare su v3 Admin Console il quale porta a: https://www.google.com/recaptcha/admin/site/717635444
 *
 * migrazione su console.cloud: https://console.cloud.google.com/security/recaptcha?project=lunasapiensconta-1738104677613
 *
 * Il settaggio domini e chiavi si trovano su
 * https://www.google.com/recaptcha/admin/site/717635444/settings
 *
 * documentazione
 * https://developers.google.com/recaptcha/docs/v3?hl=it
 *
 * ************************************ PER IL V2 con verifica
 * <html>
 *   <head>
 *     <title>reCAPTCHA demo: Simple page</title>
 *     <script src="https://www.google.com/recaptcha/enterprise.js" async defer></script>
 *   </head>
 *   <body>
 *     <form action="" method="POST">
 *       <div class="g-recaptcha" data-sitekey="6LeqDB8sAAAAACGl8_K-ag5E0YOUDKz6QXaGcLYu" data-action="LOGIN"></div>
 *       <br/>
 *       <input type="submit" value="Submit">
 *     </form>
 *   </body>
 * </html>
 *
 * Invia una richiesta POST HTTP con i dati JSON salvati all'URL riportato di seguito. Assicurati di sostituire quanto segue:
 *
 * API_KEY: La chiave API associata al progetto corrente. Learn more about authenticating with API keys .
 * https://recaptchaenterprise.googleapis.com/v1/projects/lunasapiensconta-1738104677613/assessments?key=API_KEY
 *
 */

@Service
public class RecaptchaEnterpriseService {

    @Autowired
    private GoogleRecaptchaConfig recaptchaConfig;

    private static final String API_URL =
            "https://recaptchaenterprise.googleapis.com/v1/projects/%s/assessments?key=%s";

    public boolean verify(String token) {

        try {
            RestTemplate restTemplate = new RestTemplate();

            String url = String.format(
                    API_URL,
                    recaptchaConfig.getProjectId(),
                    recaptchaConfig.getApiKey()
            );

            // JSON Body
            RecaptchaAssessmentRequest requestBody = new RecaptchaAssessmentRequest(
                    new Event(
                            token,
                            "REGISTER",
                            recaptchaConfig.getSiteKey()
                    )
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<RecaptchaAssessmentRequest> request =
                    new HttpEntity<>(requestBody, headers);

            ResponseEntity<RecaptchaAssessmentResponse> response =
                    restTemplate.postForEntity(url, request, RecaptchaAssessmentResponse.class);

            if (response.getBody() == null ||
                    response.getBody().riskAnalysis == null)
                return false;

            float score = response.getBody().riskAnalysis.score;

            return score >= 0.5f;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==== REQUEST MODELS ====

    static class RecaptchaAssessmentRequest {
        public Event event;

        public RecaptchaAssessmentRequest(Event event) {
            this.event = event;
        }
    }

    static class Event {
        public String token;
        public String expectedAction;
        public String siteKey;

        public Event(String token, String expectedAction, String siteKey) {
            this.token = token;
            this.expectedAction = expectedAction;
            this.siteKey = siteKey;
        }
    }

    // ==== RESPONSE MODELS ====

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class RecaptchaAssessmentResponse {

        @JsonProperty("riskAnalysis")
        public RiskAnalysis riskAnalysis;

        @JsonIgnoreProperties(ignoreUnknown = true)
        static class RiskAnalysis {
            @JsonProperty("score")
            public float score;
        }
    }
}

