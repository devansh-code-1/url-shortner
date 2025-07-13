package com.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class ShortenUrlRequest {
    
    @NotBlank(message = "URL cannot be blank")
    @Size(max = 2048, message = "URL cannot exceed 2048 characters")
    @Pattern(regexp = "^https?://.*", message = "URL must start with http:// or https://")
    private String url;
    
    @Size(max = 10, message = "Custom short code cannot exceed 10 characters")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "Custom short code can only contain alphanumeric characters")
    private String customShortCode;
    
    private LocalDateTime expiresAt;
    
    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;
    
    // Constructors
    public ShortenUrlRequest() {}
    
    public ShortenUrlRequest(String url) {
        this.url = url;
    }
    
    public ShortenUrlRequest(String url, String customShortCode) {
        this.url = url;
        this.customShortCode = customShortCode;
    }
    
    // Getters and Setters
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public String getCustomShortCode() {
        return customShortCode;
    }
    
    public void setCustomShortCode(String customShortCode) {
        this.customShortCode = customShortCode;
    }
    
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    @Override
    public String toString() {
        return "ShortenUrlRequest{" +
                "url='" + url + '\'' +
                ", customShortCode='" + customShortCode + '\'' +
                ", expiresAt=" + expiresAt +
                ", description='" + description + '\'' +
                '}';
    }
}