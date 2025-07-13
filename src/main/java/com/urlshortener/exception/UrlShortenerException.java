package com.urlshortener.exception;

public class UrlShortenerException extends RuntimeException {
    
    private final String errorCode;
    private final int httpStatus;
    
    public UrlShortenerException(String message) {
        super(message);
        this.errorCode = "URL_SHORTENER_ERROR";
        this.httpStatus = 500;
    }
    
    public UrlShortenerException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = 500;
    }
    
    public UrlShortenerException(String message, String errorCode, int httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
    
    public UrlShortenerException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "URL_SHORTENER_ERROR";
        this.httpStatus = 500;
    }
    
    public UrlShortenerException(String message, String errorCode, int httpStatus, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public int getHttpStatus() {
        return httpStatus;
    }
}