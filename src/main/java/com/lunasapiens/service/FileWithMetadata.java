package com.lunasapiens.service;

public class FileWithMetadata {
    private byte[] data;
    private String contentType;

    public FileWithMetadata(byte[] data, String contentType) {
        this.data = data;
        this.contentType = contentType;
    }

    public byte[] getData() {
        return data;
    }

    public String getContentType() {
        return contentType;
    }
}
