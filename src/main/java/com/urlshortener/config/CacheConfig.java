package com.urlshortener.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;

@Configuration
@EnableCaching
@EnableConfigurationProperties(UrlShortenerProperties.class)
public class CacheConfig {
    
    private final UrlShortenerProperties properties;
    
    public CacheConfig(UrlShortenerProperties properties) {
        this.properties = properties;
    }
    
    @Bean
    public CacheManager cacheManager() {
        // Using simple in-memory cache for now
        return new ConcurrentMapCacheManager("urls", "analytics");
    }
}