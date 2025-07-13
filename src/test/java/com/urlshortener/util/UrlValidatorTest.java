package com.urlshortener.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UrlValidatorTest {

    private UrlValidator urlValidator;

    @BeforeEach
    void setUp() {
        urlValidator = new UrlValidator();
    }

    @Test
    void isValidUrl_ValidHttpUrl_ReturnsTrue() {
        assertTrue(urlValidator.isValidUrl("http://example.com"));
        assertTrue(urlValidator.isValidUrl("https://example.com"));
        assertTrue(urlValidator.isValidUrl("https://www.example.com"));
        assertTrue(urlValidator.isValidUrl("https://example.com/path"));
        assertTrue(urlValidator.isValidUrl("https://example.com/path?query=value"));
    }

    @Test
    void isValidUrl_ValidLocalhostUrl_ReturnsTrue() {
        assertTrue(urlValidator.isValidUrl("http://localhost"));
        assertTrue(urlValidator.isValidUrl("https://localhost:8080"));
        assertTrue(urlValidator.isValidUrl("http://localhost:3000/path"));
    }

    @Test
    void isValidUrl_InvalidUrl_ReturnsFalse() {
        assertFalse(urlValidator.isValidUrl(null));
        assertFalse(urlValidator.isValidUrl(""));
        assertFalse(urlValidator.isValidUrl(" "));
        assertFalse(urlValidator.isValidUrl("not-a-url"));
        assertFalse(urlValidator.isValidUrl("ftp://example.com"));
    }

    @Test
    void normalizeUrl_AddsHttpsProtocol() {
        assertEquals("https://example.com", urlValidator.normalizeUrl("example.com"));
        assertEquals("https://www.example.com", urlValidator.normalizeUrl("www.example.com"));
    }

    @Test
    void normalizeUrl_RemovesTrailingSlash() {
        assertEquals("https://example.com", urlValidator.normalizeUrl("https://example.com/"));
        assertEquals("https://example.com/path", urlValidator.normalizeUrl("https://example.com/path/"));
    }

    @Test
    void normalizeUrl_HandlesNullInput() {
        assertNull(urlValidator.normalizeUrl(null));
    }

    @Test
    void isShortCodeValid_ValidShortCode_ReturnsTrue() {
        assertTrue(urlValidator.isShortCodeValid("abc123"));
        assertTrue(urlValidator.isShortCodeValid("ABC123"));
        assertTrue(urlValidator.isShortCodeValid("a1b2c3"));
        assertTrue(urlValidator.isShortCodeValid("abcdef"));
    }

    @Test
    void isShortCodeValid_InvalidShortCode_ReturnsFalse() {
        assertFalse(urlValidator.isShortCodeValid(null));
        assertFalse(urlValidator.isShortCodeValid(""));
        assertFalse(urlValidator.isShortCodeValid(" "));
        assertFalse(urlValidator.isShortCodeValid("ab")); // too short
        assertFalse(urlValidator.isShortCodeValid("abcdefghijk")); // too long
        assertFalse(urlValidator.isShortCodeValid("abc-123")); // contains hyphen
        assertFalse(urlValidator.isShortCodeValid("abc_123")); // contains underscore
        assertFalse(urlValidator.isShortCodeValid("abc 123")); // contains space
    }
}