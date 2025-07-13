package com.urlshortener.dto;

import java.time.LocalDateTime;

public class ShortenUrlResponse {
    
    private String originalUrl;
    private String shortUrl;
    private String shortCode;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
    private String description;
    
    // Constructors
    public ShortenUrlResponse() {}
    
    public ShortenUrlResponse(String originalUrl, String shortUrl, String shortCode) {
        this.originalUrl = originalUrl;
        this.shortUrl = shortUrl;
        this.shortCode = shortCode;
    }
    
    // Getters and Setters
    public String getOriginalUrl() {
        return originalUrl;
    }
    
    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }
    
    public String getShortUrl() {
        return shortUrl;
    }
    
    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }
    
    public String getShortCode() {
        return shortCode;
    }
    
    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }
    
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    @Override
    public String toString() {
        return "ShortenUrlResponse{" +
                "originalUrl='" + originalUrl + '\'' +
                ", shortUrl='" + shortUrl + '\'' +
                ", shortCode='" + shortCode + '\'' +
                ", expiresAt=" + expiresAt +
                ", createdAt=" + createdAt +
                ", description='" + description + '\'' +
                '}';
    }
}