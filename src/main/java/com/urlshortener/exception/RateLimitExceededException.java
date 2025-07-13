package com.urlshortener.exception;

public class RateLimitExceededException extends UrlShortenerException {
    
    public RateLimitExceededException(String message) {
        super(message, "RATE_LIMIT_EXCEEDED", 429);
    }
    
    public RateLimitExceededException(String message, String errorCode) {
        super(message, errorCode, 429);
    }
}