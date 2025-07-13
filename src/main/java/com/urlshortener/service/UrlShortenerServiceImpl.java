package com.urlshortener.service;

import com.urlshortener.domain.UrlEntity;
import com.urlshortener.dto.ShortenUrlRequest;
import com.urlshortener.dto.ShortenUrlResponse;
import com.urlshortener.dto.UrlAnalyticsResponse;
import com.urlshortener.exception.*;
import com.urlshortener.repository.UrlRepository;
import com.urlshortener.util.ShortCodeGenerator;
import com.urlshortener.util.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class UrlShortenerServiceImpl implements UrlShortenerService {
    
    private static final Logger logger = LoggerFactory.getLogger(UrlShortenerServiceImpl.class);
    private static final int MAX_RETRY_ATTEMPTS = 5;
    
    private final UrlRepository urlRepository;
    private final ShortCodeGenerator shortCodeGenerator;
    private final UrlValidator urlValidator;
    
    @Value("${urlshortener.base-url}")
    private String baseUrl;
    
    public UrlShortenerServiceImpl(UrlRepository urlRepository, 
                                   ShortCodeGenerator shortCodeGenerator,
                                   UrlValidator urlValidator) {
        this.urlRepository = urlRepository;
        this.shortCodeGenerator = shortCodeGenerator;
        this.urlValidator = urlValidator;
    }
    
    @Override
    public ShortenUrlResponse shortenUrl(ShortenUrlRequest request) {
        logger.info("Shortening URL: {}", request.getUrl());
        
        // Validate URL
        if (!urlValidator.isValidUrl(request.getUrl())) {
            throw new InvalidUrlException(request.getUrl());
        }
        
        String normalizedUrl = urlValidator.normalizeUrl(request.getUrl());
        
        // Check if URL already exists
        Optional<UrlEntity> existingUrl = urlRepository.findByOriginalUrl(normalizedUrl);
        if (existingUrl.isPresent() && existingUrl.get().isAccessible()) {
            logger.info("URL already exists, returning existing short code");
            return mapToShortenUrlResponse(existingUrl.get());
        }
        
        // Generate or validate custom short code
        String shortCode = generateOrValidateShortCode(request.getCustomShortCode());
        
        // Create new URL entity
        UrlEntity urlEntity = new UrlEntity(normalizedUrl, shortCode, request.getExpiresAt());
        urlEntity.setDescription(request.getDescription());
        
        // Save to database
        UrlEntity savedEntity = urlRepository.save(urlEntity);
        
        logger.info("URL shortened successfully: {} -> {}", normalizedUrl, shortCode);
        return mapToShortenUrlResponse(savedEntity);
    }
    
    @Override
    @Cacheable(value = "urls", key = "#shortCode")
    public String getOriginalUrl(String shortCode) {
        logger.info("Getting original URL for short code: {}", shortCode);
        
        UrlEntity urlEntity = urlRepository.findByShortCode(shortCode)
            .orElseThrow(() -> new UrlNotFoundException(shortCode));
        
        if (!urlEntity.getIsActive()) {
            throw new UrlNotFoundException(shortCode);
        }
        
        if (urlEntity.isExpired()) {
            throw new UrlExpiredException(shortCode);
        }
        
        // Increment click count asynchronously
        incrementClickCountAsync(shortCode);
        
        return urlEntity.getOriginalUrl();
    }
    
    @Override
    @Cacheable(value = "analytics", key = "#shortCode")
    public UrlAnalyticsResponse getAnalytics(String shortCode) {
        logger.info("Getting analytics for short code: {}", shortCode);
        
        UrlEntity urlEntity = urlRepository.findByShortCode(shortCode)
            .orElseThrow(() -> new UrlNotFoundException(shortCode));
        
        return mapToAnalyticsResponse(urlEntity);
    }
    
    @Override
    @CacheEvict(value = {"urls", "analytics"}, key = "#shortCode")
    public void deleteUrl(String shortCode) {
        logger.info("Deleting URL with short code: {}", shortCode);
        
        UrlEntity urlEntity = urlRepository.findByShortCode(shortCode)
            .orElseThrow(() -> new UrlNotFoundException(shortCode));
        
        urlEntity.setIsActive(false);
        urlRepository.save(urlEntity);
        
        logger.info("URL deactivated successfully: {}", shortCode);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isUrlExists(String shortCode) {
        return urlRepository.existsByShortCode(shortCode);
    }
    
    @Override
    public void cleanupExpiredUrls() {
        logger.info("Starting cleanup of expired URLs");
        
        int deactivatedCount = urlRepository.deactivateExpiredUrls(LocalDateTime.now());
        
        logger.info("Cleanup completed. Deactivated {} expired URLs", deactivatedCount);
    }
    
    private String generateOrValidateShortCode(String customShortCode) {
        if (customShortCode != null && !customShortCode.trim().isEmpty()) {
            // Validate custom short code
            if (!urlValidator.isShortCodeValid(customShortCode)) {
                throw new InvalidUrlException("Invalid custom short code: " + customShortCode);
            }
            
            // Check if custom short code already exists
            if (urlRepository.existsByShortCode(customShortCode)) {
                throw new ShortCodeAlreadyExistsException(customShortCode);
            }
            
            return customShortCode;
        }
        
        // Generate unique short code
        String shortCode;
        int attempts = 0;
        
        do {
            shortCode = shortCodeGenerator.generateShortCode();
            attempts++;
            
            if (attempts > MAX_RETRY_ATTEMPTS) {
                throw new UrlShortenerException("Failed to generate unique short code after " + MAX_RETRY_ATTEMPTS + " attempts");
            }
        } while (urlRepository.existsByShortCode(shortCode));
        
        return shortCode;
    }
    
    private void incrementClickCountAsync(String shortCode) {
        try {
            urlRepository.incrementClickCount(shortCode);
        } catch (Exception e) {
            logger.error("Failed to increment click count for short code: {}", shortCode, e);
            // Don't throw exception as this is not critical for URL redirection
        }
    }
    
    private ShortenUrlResponse mapToShortenUrlResponse(UrlEntity urlEntity) {
        ShortenUrlResponse response = new ShortenUrlResponse();
        response.setOriginalUrl(urlEntity.getOriginalUrl());
        response.setShortCode(urlEntity.getShortCode());
        response.setShortUrl(baseUrl + "/" + urlEntity.getShortCode());
        response.setExpiresAt(urlEntity.getExpiresAt());
        response.setCreatedAt(urlEntity.getCreatedAt());
        response.setDescription(urlEntity.getDescription());
        return response;
    }
    
    private UrlAnalyticsResponse mapToAnalyticsResponse(UrlEntity urlEntity) {
        UrlAnalyticsResponse response = new UrlAnalyticsResponse();
        response.setOriginalUrl(urlEntity.getOriginalUrl());
        response.setShortCode(urlEntity.getShortCode());
        response.setShortUrl(baseUrl + "/" + urlEntity.getShortCode());
        response.setClickCount(urlEntity.getClickCount());
        response.setCreatedAt(urlEntity.getCreatedAt());
        response.setExpiresAt(urlEntity.getExpiresAt());
        response.setIsActive(urlEntity.getIsActive());
        response.setDescription(urlEntity.getDescription());
        return response;
    }
}