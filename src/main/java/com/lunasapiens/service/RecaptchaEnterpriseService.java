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
 * Nome Progetto reCaptcha v2 lunasapiens.com
 *
 *
 * Credenziali Api Key gooogle
 * https://console.cloud.google.com/apis/credentials?project=lunasapiens-1718910466659
 *
 * Il settaggio domini e chiave recsaptcha si trovano su
 * https://console.cloud.google.com/security/recaptcha?project=lunasapiens-1718910466659
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

