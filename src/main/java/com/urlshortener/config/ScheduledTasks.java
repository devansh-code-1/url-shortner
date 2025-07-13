package com.urlshortener.config;

import com.urlshortener.service.UrlShortenerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class ScheduledTasks {
    
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);
    
    private final UrlShortenerService urlShortenerService;
    
    public ScheduledTasks(UrlShortenerService urlShortenerService) {
        this.urlShortenerService = urlShortenerService;
    }
    
    @Scheduled(fixedRate = 3600000) // Run every hour
    public void cleanupExpiredUrls() {
        logger.info("Starting scheduled cleanup of expired URLs");
        try {
            urlShortenerService.cleanupExpiredUrls();
            logger.info("Scheduled cleanup completed successfully");
        } catch (Exception e) {
            logger.error("Error during scheduled cleanup", e);
        }
    }
}