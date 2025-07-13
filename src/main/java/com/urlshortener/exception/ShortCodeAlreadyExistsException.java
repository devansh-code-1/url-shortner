package com.urlshortener.exception;

public class ShortCodeAlreadyExistsException extends UrlShortenerException {
    
    public ShortCodeAlreadyExistsException(String shortCode) {
        super("Short code '" + shortCode + "' already exists", "SHORT_CODE_EXISTS", 409);
    }
    
    public ShortCodeAlreadyExistsException(String message, String errorCode) {
        super(message, errorCode, 409);
    }
}