package com.lunasapiens.config;

public class S3ClientConfig {

    String accessKey;
    String secretKey;
    String region;
    String bucketName;

    public S3ClientConfig(String accessKey, String secretKey, String region, String bucketName) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.region = region;
        this.bucketName = bucketName;

    }

    public String getAccessKey() {
        return accessKey;
    }
    public String getSecretKey() {
        return secretKey;
    }
    public String getRegion() { return region; }
    public String getBucketName() { return bucketName; }
}
