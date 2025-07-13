package com.urlshortener;

import com.urlshortener.dto.ShortenUrlRequest;
import com.urlshortener.dto.ShortenUrlResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class UrlShortenerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void completeUrlShortenerFlow_ShortenAndRedirect_Success() throws Exception {
        // Create shorten request
        ShortenUrlRequest request = new ShortenUrlRequest();
        request.setUrl("https://example.com");

        // Shorten the URL
        MvcResult result = mockMvc.perform(post("/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.originalUrl").value("https://example.com"))
                .andExpect(jsonPath("$.shortCode").exists())
                .andExpect(jsonPath("$.shortUrl").exists())
                .andReturn();

        // Extract the short code from response
        String responseBody = result.getResponse().getContentAsString();
        ShortenUrlResponse response = objectMapper.readValue(responseBody, ShortenUrlResponse.class);
        String shortCode = response.getShortCode();

        // Test redirection
        mockMvc.perform(get("/" + shortCode))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://example.com"));

        // Test analytics
        mockMvc.perform(get("/analytics/" + shortCode))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.originalUrl").value("https://example.com"))
                .andExpect(jsonPath("$.shortCode").value(shortCode))
                .andExpect(jsonPath("$.clickCount").value(1))
                .andExpect(jsonPath("$.isActive").value(true));

        // Test deletion
        mockMvc.perform(delete("/" + shortCode))
                .andExpect(status().isNoContent());
    }

    @Test
    void shortenUrl_WithCustomShortCode_Success() throws Exception {
        ShortenUrlRequest request = new ShortenUrlRequest();
        request.setUrl("https://example.com");
        request.setCustomShortCode("custom123");

        mockMvc.perform(post("/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.originalUrl").value("https://example.com"))
                .andExpect(jsonPath("$.shortCode").value("custom123"))
                .andExpect(jsonPath("$.shortUrl").exists());
    }

    @Test
    void shortenUrl_InvalidUrl_ReturnsBadRequest() throws Exception {
        ShortenUrlRequest request = new ShortenUrlRequest();
        request.setUrl("invalid-url");

        mockMvc.perform(post("/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void redirectToOriginalUrl_NonExistentShortCode_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/nonexistent"))
                .andExpect(status().isNotFound());
    }
}