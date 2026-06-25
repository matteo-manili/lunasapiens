package com.lunasapiens.dto;

import java.time.LocalDateTime;

public class PageVisitDTO {

    private Long id;
    private String path;
    private String ip;
    private String sessionId;
    private LocalDateTime startTime;
    private LocalDateTime lastSeen;
    private String userAgent;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(LocalDateTime lastSeen) {
        this.lastSeen = lastSeen;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }



    public long getSeconds() {
        if (startTime == null) return 0;
        LocalDateTime end = lastSeen != null ? lastSeen : startTime;
        return java.time.Duration.between(startTime, end).getSeconds();
    }


    public String getFormattedSeconds() {
        long s = getSeconds();

        long minutes = s / 60;
        long secs = s % 60;

        return (minutes > 0)
                ? minutes + "m " + secs + "s"
                : secs + "s";
    }



}
