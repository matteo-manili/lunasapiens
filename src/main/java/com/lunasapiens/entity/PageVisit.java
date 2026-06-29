package com.lunasapiens.entity;

import com.lunasapiens.utils.Utils;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "page_visit")
public class PageVisit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sessionId;
    private String path;
    private String ip;
    @Column(columnDefinition = "TEXT")
    private String userAgent;
    @Column(columnDefinition = "TEXT")
    private String referer;
    private String acceptLanguage;

    private LocalDateTime startTime;
    private LocalDateTime lastSeen;
    private LocalDateTime endTime;

    public PageVisit() {}

    public PageVisit(String sessionId, String path, String ip, String userAgent, String referer, String acceptLanguage) {
        this.sessionId = sessionId;
        this.path = path;
        this.ip = ip;
        this.userAgent = userAgent;
        this.referer = referer;
        this.acceptLanguage = acceptLanguage;
        this.startTime = Utils.getNowRomeEurope().toLocalDateTime();
        this.lastSeen = this.startTime;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
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

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getReferer() { return referer; }

    public void setReferer(String referer) { this.referer = referer; }

    public String getAcceptLanguage() { return acceptLanguage; }

    public void setAcceptLanguage(String acceptLanguage) { this.acceptLanguage = acceptLanguage; }

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

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

}