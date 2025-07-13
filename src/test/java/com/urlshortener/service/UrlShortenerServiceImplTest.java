package com.urlshortener.service;

import com.urlshortener.domain.UrlEntity;
import com.urlshortener.dto.ShortenUrlRequest;
import com.urlshortener.dto.ShortenUrlResponse;
import com.urlshortener.exception.InvalidUrlException;
import com.urlshortener.exception.UrlNotFoundException;
import com.urlshortener.repository.UrlRepository;
import com.urlshortener.util.ShortCodeGenerator;
import com.urlshortener.util.UrlValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlShortenerServiceImplTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private ShortCodeGenerator shortCodeGenerator;

    @Mock
    private UrlValidator urlValidator;

    @InjectMocks
    private UrlShortenerServiceImpl urlShortenerService;

    private ShortenUrlRequest validRequest;
    private UrlEntity urlEntity;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(urlShortenerService, "baseUrl", "http://localhost:8080/api/v1");
        
        validRequest = new ShortenUrlRequest();
        validRequest.setUrl("https://example.com");
        
        urlEntity = new UrlEntity();
        urlEntity.setId(1L);
        urlEntity.setOriginalUrl("https://example.com");
        urlEntity.setShortCode("abc123");
        urlEntity.setIsActive(true);
        urlEntity.setClickCount(0L);
        urlEntity.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void shortenUrl_ValidUrl_ReturnsResponse() {
        // Given
        when(urlValidator.isValidUrl(anyString())).thenReturn(true);
        when(urlValidator.normalizeUrl(anyString())).thenReturn("https://example.com");
        when(urlRepository.findByOriginalUrl(anyString())).thenReturn(Optional.empty());
        when(shortCodeGenerator.generateShortCode()).thenReturn("abc123");
        when(urlRepository.existsByShortCode(anyString())).thenReturn(false);
        when(urlRepository.save(any(UrlEntity.class))).thenReturn(urlEntity);

        // When
        ShortenUrlResponse response = urlShortenerService.shortenUrl(validRequest);

        // Then
        assertNotNull(response);
        assertEquals("https://example.com", response.getOriginalUrl());
        assertEquals("abc123", response.getShortCode());
        assertEquals("http://localhost:8080/api/v1/abc123", response.getShortUrl());
        
        verify(urlRepository, times(1)).save(any(UrlEntity.class));
    }

    @Test
    void shortenUrl_InvalidUrl_ThrowsException() {
        // Given
        when(urlValidator.isValidUrl(anyString())).thenReturn(false);

        // When & Then
        assertThrows(InvalidUrlException.class, () -> {
            urlShortenerService.shortenUrl(validRequest);
        });
        
        verify(urlRepository, never()).save(any(UrlEntity.class));
    }

    @Test
    void getOriginalUrl_ExistingShortCode_ReturnsUrl() {
        // Given
        when(urlRepository.findByShortCode("abc123")).thenReturn(Optional.of(urlEntity));

        // When
        String originalUrl = urlShortenerService.getOriginalUrl("abc123");

        // Then
        assertEquals("https://example.com", originalUrl);
        verify(urlRepository, times(1)).incrementClickCount("abc123");
    }

    @Test
    void getOriginalUrl_NonExistingShortCode_ThrowsException() {
        // Given
        when(urlRepository.findByShortCode("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UrlNotFoundException.class, () -> {
            urlShortenerService.getOriginalUrl("nonexistent");
        });
    }

    @Test
    void deleteUrl_ExistingShortCode_DeactivatesUrl() {
        // Given
        when(urlRepository.findByShortCode("abc123")).thenReturn(Optional.of(urlEntity));
        when(urlRepository.save(any(UrlEntity.class))).thenReturn(urlEntity);

        // When
        urlShortenerService.deleteUrl("abc123");

        // Then
        verify(urlRepository, times(1)).save(any(UrlEntity.class));
        assertFalse(urlEntity.getIsActive());
    }

    @Test
    void isUrlExists_ExistingShortCode_ReturnsTrue() {
        // Given
        when(urlRepository.existsByShortCode("abc123")).thenReturn(true);

        // When
        boolean exists = urlShortenerService.isUrlExists("abc123");

        // Then
        assertTrue(exists);
    }

    @Test
    void isUrlExists_NonExistingShortCode_ReturnsFalse() {
        // Given
        when(urlRepository.existsByShortCode("nonexistent")).thenReturn(false);

        // When
        boolean exists = urlShortenerService.isUrlExists("nonexistent");

        // Then
        assertFalse(exists);
    }
}