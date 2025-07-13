package com.urlshortener.exception;

public class UrlExpiredException extends UrlShortenerException {
    
    public UrlExpiredException(String shortCode) {
        super("URL with short code '" + shortCode + "' has expired", "URL_EXPIRED", 410);
    }
    
    public UrlExpiredException(String message, String errorCode) {
        super(message, errorCode, 410);
    }
}