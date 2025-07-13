package com.urlshortener.exception;

public class UrlNotFoundException extends UrlShortenerException {
    
    public UrlNotFoundException(String shortCode) {
        super("URL with short code '" + shortCode + "' not found", "URL_NOT_FOUND", 404);
    }
    
    public UrlNotFoundException(String message, String errorCode) {
        super(message, errorCode, 404);
    }
}