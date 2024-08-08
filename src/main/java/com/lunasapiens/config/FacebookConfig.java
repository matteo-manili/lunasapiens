package com.lunasapiens.config;

public class FacebookConfig {


    private String appId;
    private String appSecret;
    private String pageId;



    public FacebookConfig(String appId, String appSecret, String pageId) {
        this.appId = appId;
        this.appSecret = appSecret;
        this.pageId = pageId;
    }



    public String getAppId() {
        return appId;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public String getPageId() { return pageId; }
}
