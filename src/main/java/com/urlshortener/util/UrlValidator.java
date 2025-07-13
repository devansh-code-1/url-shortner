package com.urlshortener.util;

import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

@Component
public class UrlValidator {
    
    private static final Pattern URL_PATTERN = Pattern.compile(
        "^(https?://)?" +                           // Protocol (optional)
        "((([a-z\\d]([a-z\\d-]*[a-z\\d])*)\\.)+[a-z]{2,}|" + // Domain name
        "((\\d{1,3}\\.){3}\\d{1,3}))" +              // IP address
        "(\\:\\d+)?(/[-a-z\\d%_.~+]*)*" +           // Port and path
        "(\\?[;&a-z\\d%_.~+=-]*)?" +                // Query string
        "(\\#[-a-z\\d_]*)?$",                       // Fragment
        Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern LOCALHOST_PATTERN = Pattern.compile(
        "^(https?://)?localhost(:\\d+)?(/.*)?$",
        Pattern.CASE_INSENSITIVE
    );
    
    public boolean isValidUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }
        
        String trimmedUrl = url.trim();
        
        // Check for localhost URLs (for development/testing)
        if (LOCALHOST_PATTERN.matcher(trimmedUrl).matches()) {
            return true;
        }
        
        // Check against regex pattern
        if (!URL_PATTERN.matcher(trimmedUrl).matches()) {
            return false;
        }
        
        // Additional validation using Java's URL class
        try {
            String urlToValidate = trimmedUrl;
            if (!urlToValidate.startsWith("http://") && !urlToValidate.startsWith("https://")) {
                urlToValidate = "http://" + urlToValidate;
            }
            
            new URL(urlToValidate);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }
    
    public String normalizeUrl(String url) {
        if (url == null) {
            return null;
        }
        
        String normalizedUrl = url.trim();
        
        // Add protocol if missing
        if (!normalizedUrl.startsWith("http://") && !normalizedUrl.startsWith("https://")) {
            normalizedUrl = "https://" + normalizedUrl;
        }
        
        // Remove trailing slash if present
        if (normalizedUrl.endsWith("/")) {
            normalizedUrl = normalizedUrl.substring(0, normalizedUrl.length() - 1);
        }
        
        return normalizedUrl;
    }
    
    public boolean isShortCodeValid(String shortCode) {
        if (shortCode == null || shortCode.trim().isEmpty()) {
            return false;
        }
        
        String trimmedCode = shortCode.trim();
        
        // Check length (between 3 and 10 characters)
        if (trimmedCode.length() < 3 || trimmedCode.length() > 10) {
            return false;
        }
        
        // Check if contains only alphanumeric characters
        return trimmedCode.matches("^[a-zA-Z0-9]+$");
    }
}