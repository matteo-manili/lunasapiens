package com.lunasapiens.config;

public class FacebookConfig {

    private String version;
    private String appId;
    private String appSecret;
    private String accessToken;

    private String pageAccessToken;


    public FacebookConfig(String version, String appId, String appSecret, String accessToken, String pageAccessToken) {
        this.version = version;
        this.appId = appId;
        this.appSecret = appSecret;
        this.accessToken = accessToken;
        this.pageAccessToken = pageAccessToken;
    }

    public String getVersion() {
        return version;
    }

    public String getAppId() {
        return appId;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getPageAccessToken() {
        return pageAccessToken;
    }
}
