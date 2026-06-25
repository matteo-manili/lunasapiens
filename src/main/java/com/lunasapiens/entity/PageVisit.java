package com.lunasapiens.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="page_visit")
public class PageVisit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String path;
    private String ip;
    private String userAgent;
    private String sessionId;
    private LocalDateTime startTime;
    private LocalDateTime lastHeartbeat;
    private Integer secondsSpent;


    public PageVisit(){}

    public PageVisit(
            String path,
            String ip,
            String userAgent,
            String sessionId
    ){
        this.path=path;
        this.ip=ip;
        this.userAgent=userAgent;
        this.sessionId=sessionId;
        this.startTime=LocalDateTime.now();
        this.lastHeartbeat=LocalDateTime.now();
        this.secondsSpent=0;
    }


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

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
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

    public LocalDateTime getLastHeartbeat() {
        return lastHeartbeat;
    }

    public void setLastHeartbeat(LocalDateTime lastHeartbeat) {
        this.lastHeartbeat = lastHeartbeat;
    }

    public Integer getSecondsSpent() {
        return secondsSpent;
    }

    public void setSecondsSpent(Integer secondsSpent) {
        this.secondsSpent = secondsSpent;
    }
}