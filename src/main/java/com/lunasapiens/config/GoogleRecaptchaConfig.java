package com.lunasapiens.config;

public class GoogleRecaptchaConfig {


    private String apiKey;
    private String siteKey;
    private String projectId;


    public GoogleRecaptchaConfig(String apiKey, String siteKey, String projectId) {
        this.apiKey = apiKey;
        this.siteKey = siteKey;
        this.projectId = projectId;
    }


    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getSiteKey() {
        return siteKey;
    }

    public void setSiteKey(String siteKey) {
        this.siteKey = siteKey;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
}
