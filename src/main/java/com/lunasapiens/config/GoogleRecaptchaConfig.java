package com.lunasapiens.config;

public class GoogleRecaptchaConfig {



    private String secretKey;
    private String publicKey;

    public GoogleRecaptchaConfig(String secretKey, String publicKey) {
        this.secretKey = secretKey;
        this.publicKey = publicKey;
    }

    public String getSecretKey() {
        return secretKey;
    }
    public String getPublicKey() { return publicKey; }
}
