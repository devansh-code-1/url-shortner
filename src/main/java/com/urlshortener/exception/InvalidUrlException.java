package com.urlshortener.exception;

public class InvalidUrlException extends UrlShortenerException {
    
    public InvalidUrlException(String url) {
        super("Invalid URL: " + url, "INVALID_URL", 400);
    }
    
    public InvalidUrlException(String message, String errorCode) {
        super(message, errorCode, 400);
    }
}