package com.urlshortener.controller;

import com.urlshortener.dto.ShortenUrlRequest;
import com.urlshortener.dto.ShortenUrlResponse;
import com.urlshortener.dto.UrlAnalyticsResponse;
import com.urlshortener.service.UrlShortenerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UrlShortenerController.class)
@ActiveProfiles("test")
class UrlShortenerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UrlShortenerService urlShortenerService;

    @Autowired
    private ObjectMapper objectMapper;

    private ShortenUrlRequest validRequest;
    private ShortenUrlResponse validResponse;
    private UrlAnalyticsResponse analyticsResponse;

    @BeforeEach
    void setUp() {
        validRequest = new ShortenUrlRequest();
        validRequest.setUrl("https://example.com");
        
        validResponse = new ShortenUrlResponse();
        validResponse.setOriginalUrl("https://example.com");
        validResponse.setShortCode("abc123");
        validResponse.setShortUrl("http://localhost:8080/api/v1/abc123");
        validResponse.setCreatedAt(LocalDateTime.now());
        
        analyticsResponse = new UrlAnalyticsResponse();
        analyticsResponse.setOriginalUrl("https://example.com");
        analyticsResponse.setShortCode("abc123");
        analyticsResponse.setShortUrl("http://localhost:8080/api/v1/abc123");
        analyticsResponse.setClickCount(5L);
        analyticsResponse.setIsActive(true);
    }

    @Test
    void shortenUrl_ValidRequest_ReturnsCreated() throws Exception {
        when(urlShortenerService.shortenUrl(any(ShortenUrlRequest.class))).thenReturn(validResponse);

        mockMvc.perform(post("/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.originalUrl").value("https://example.com"))
                .andExpect(jsonPath("$.shortCode").value("abc123"))
                .andExpect(jsonPath("$.shortUrl").value("http://localhost:8080/api/v1/abc123"));
    }

    @Test
    void shortenUrl_InvalidRequest_ReturnsBadRequest() throws Exception {
        ShortenUrlRequest invalidRequest = new ShortenUrlRequest();
        invalidRequest.setUrl(""); // invalid URL

        mockMvc.perform(post("/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void redirectToOriginalUrl_ValidShortCode_ReturnsRedirect() throws Exception {
        when(urlShortenerService.getOriginalUrl("abc123")).thenReturn("https://example.com");

        mockMvc.perform(get("/abc123"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://example.com"));
    }

    @Test
    void getAnalytics_ValidShortCode_ReturnsAnalytics() throws Exception {
        when(urlShortenerService.getAnalytics("abc123")).thenReturn(analyticsResponse);

        mockMvc.perform(get("/analytics/abc123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.originalUrl").value("https://example.com"))
                .andExpect(jsonPath("$.shortCode").value("abc123"))
                .andExpect(jsonPath("$.clickCount").value(5))
                .andExpect(jsonPath("$.isActive").value(true));
    }

    @Test
    void deleteUrl_ValidShortCode_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/abc123"))
                .andExpect(status().isNoContent());
    }

    @Test
    void health_ReturnsOk() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("URL Shortener Service is running"));
    }
}