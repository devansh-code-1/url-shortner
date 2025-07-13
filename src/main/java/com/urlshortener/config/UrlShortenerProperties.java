package com.urlshortener.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "urlshortener")
public class UrlShortenerProperties {
    
    private String baseUrl = "http://localhost:8080/api/v1";
    private ShortCode shortCode = new ShortCode();
    private Cache cache = new Cache();
    private RateLimit rateLimit = new RateLimit();
    
    public String getBaseUrl() {
        return baseUrl;
    }
    
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
    
    public ShortCode getShortCode() {
        return shortCode;
    }
    
    public void setShortCode(ShortCode shortCode) {
        this.shortCode = shortCode;
    }
    
    public Cache getCache() {
        return cache;
    }
    
    public void setCache(Cache cache) {
        this.cache = cache;
    }
    
    public RateLimit getRateLimit() {
        return rateLimit;
    }
    
    public void setRateLimit(RateLimit rateLimit) {
        this.rateLimit = rateLimit;
    }
    
    public static class ShortCode {
        private int length = 6;
        private String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        
        public int getLength() {
            return length;
        }
        
        public void setLength(int length) {
            this.length = length;
        }
        
        public String getCharacters() {
            return characters;
        }
        
        public void setCharacters(String characters) {
            this.characters = characters;
        }
    }
    
    public static class Cache {
        private int expirationHours = 24;
        
        public int getExpirationHours() {
            return expirationHours;
        }
        
        public void setExpirationHours(int expirationHours) {
            this.expirationHours = expirationHours;
        }
    }
    
    public static class RateLimit {
        private int requestsPerMinute = 100;
        private int requestsPerHour = 1000;
        
        public int getRequestsPerMinute() {
            return requestsPerMinute;
        }
        
        public void setRequestsPerMinute(int requestsPerMinute) {
            this.requestsPerMinute = requestsPerMinute;
        }
        
        public int getRequestsPerHour() {
            return requestsPerHour;
        }
        
        public void setRequestsPerHour(int requestsPerHour) {
            this.requestsPerHour = requestsPerHour;
        }
    }
}