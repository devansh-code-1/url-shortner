package com.urlshortener.service;

import com.urlshortener.dto.ShortenUrlRequest;
import com.urlshortener.dto.ShortenUrlResponse;
import com.urlshortener.dto.UrlAnalyticsResponse;

public interface UrlShortenerService {
    
    ShortenUrlResponse shortenUrl(ShortenUrlRequest request);
    
    String getOriginalUrl(String shortCode);
    
    UrlAnalyticsResponse getAnalytics(String shortCode);
    
    void deleteUrl(String shortCode);
    
    boolean isUrlExists(String shortCode);
    
    void cleanupExpiredUrls();
}