package com.urlshortener.controller;

import com.urlshortener.dto.ShortenUrlRequest;
import com.urlshortener.dto.ShortenUrlResponse;
import com.urlshortener.dto.UrlAnalyticsResponse;
import com.urlshortener.service.UrlShortenerService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping
public class UrlShortenerController {
    
    private static final Logger logger = LoggerFactory.getLogger(UrlShortenerController.class);
    
    private final UrlShortenerService urlShortenerService;
    
    public UrlShortenerController(UrlShortenerService urlShortenerService) {
        this.urlShortenerService = urlShortenerService;
    }
    
    @PostMapping("/shorten")
    public ResponseEntity<ShortenUrlResponse> shortenUrl(@Valid @RequestBody ShortenUrlRequest request) {
        logger.info("Received request to shorten URL: {}", request.getUrl());
        
        ShortenUrlResponse response = urlShortenerService.shortenUrl(request);
        
        logger.info("URL shortened successfully: {}", response.getShortCode());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String shortCode) {
        logger.info("Received request to redirect short code: {}", shortCode);
        
        String originalUrl = urlShortenerService.getOriginalUrl(shortCode);
        
        logger.info("Redirecting to original URL: {}", originalUrl);
        return ResponseEntity.status(HttpStatus.FOUND)
            .location(URI.create(originalUrl))
            .build();
    }
    
    @GetMapping("/analytics/{shortCode}")
    public ResponseEntity<UrlAnalyticsResponse> getAnalytics(@PathVariable String shortCode) {
        logger.info("Received request to get analytics for short code: {}", shortCode);
        
        UrlAnalyticsResponse response = urlShortenerService.getAnalytics(shortCode);
        
        logger.info("Analytics retrieved successfully for short code: {}", shortCode);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{shortCode}")
    public ResponseEntity<Void> deleteUrl(@PathVariable String shortCode) {
        logger.info("Received request to delete short code: {}", shortCode);
        
        urlShortenerService.deleteUrl(shortCode);
        
        logger.info("URL deleted successfully: {}", shortCode);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("URL Shortener Service is running");
    }
}