package com.urlshortener.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class ShortCodeGenerator {
    
    @Value("${urlshortener.short-code.length:6}")
    private int shortCodeLength;
    
    @Value("${urlshortener.short-code.characters:abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789}")
    private String allowedCharacters;
    
    private final SecureRandom random = new SecureRandom();
    
    public String generateShortCode() {
        StringBuilder shortCode = new StringBuilder(shortCodeLength);
        
        for (int i = 0; i < shortCodeLength; i++) {
            int randomIndex = random.nextInt(allowedCharacters.length());
            shortCode.append(allowedCharacters.charAt(randomIndex));
        }
        
        return shortCode.toString();
    }
    
    public String generateShortCode(int length) {
        if (length <= 0 || length > 10) {
            throw new IllegalArgumentException("Short code length must be between 1 and 10");
        }
        
        StringBuilder shortCode = new StringBuilder(length);
        
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(allowedCharacters.length());
            shortCode.append(allowedCharacters.charAt(randomIndex));
        }
        
        return shortCode.toString();
    }
    
    public boolean isValidShortCode(String shortCode) {
        if (shortCode == null || shortCode.trim().isEmpty()) {
            return false;
        }
        
        String trimmedCode = shortCode.trim();
        
        // Check length
        if (trimmedCode.length() < 3 || trimmedCode.length() > 10) {
            return false;
        }
        
        // Check if all characters are allowed
        for (char c : trimmedCode.toCharArray()) {
            if (allowedCharacters.indexOf(c) == -1) {
                return false;
            }
        }
        
        return true;
    }
}