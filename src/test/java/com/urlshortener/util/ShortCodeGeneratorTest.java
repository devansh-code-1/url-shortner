package com.urlshortener.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class ShortCodeGeneratorTest {

    private ShortCodeGenerator shortCodeGenerator;

    @BeforeEach
    void setUp() {
        shortCodeGenerator = new ShortCodeGenerator();
        ReflectionTestUtils.setField(shortCodeGenerator, "shortCodeLength", 6);
        ReflectionTestUtils.setField(shortCodeGenerator, "allowedCharacters", 
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
    }

    @Test
    void generateShortCode_DefaultLength_ReturnsCorrectLength() {
        String shortCode = shortCodeGenerator.generateShortCode();
        
        assertEquals(6, shortCode.length());
        assertTrue(shortCode.matches("^[a-zA-Z0-9]+$"));
    }

    @Test
    void generateShortCode_CustomLength_ReturnsCorrectLength() {
        String shortCode = shortCodeGenerator.generateShortCode(8);
        
        assertEquals(8, shortCode.length());
        assertTrue(shortCode.matches("^[a-zA-Z0-9]+$"));
    }

    @Test
    void generateShortCode_InvalidLength_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            shortCodeGenerator.generateShortCode(0);
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            shortCodeGenerator.generateShortCode(11);
        });
    }

    @Test
    void generateShortCode_MultipleGenerations_ProduceDifferentResults() {
        String shortCode1 = shortCodeGenerator.generateShortCode();
        String shortCode2 = shortCodeGenerator.generateShortCode();
        
        // While theoretically possible to be the same, it's extremely unlikely
        // This test might occasionally fail due to random generation
        assertNotEquals(shortCode1, shortCode2);
    }

    @Test
    void isValidShortCode_ValidShortCode_ReturnsTrue() {
        assertTrue(shortCodeGenerator.isValidShortCode("abc123"));
        assertTrue(shortCodeGenerator.isValidShortCode("ABC123"));
        assertTrue(shortCodeGenerator.isValidShortCode("abcdef"));
        assertTrue(shortCodeGenerator.isValidShortCode("123456"));
    }

    @Test
    void isValidShortCode_InvalidShortCode_ReturnsFalse() {
        assertFalse(shortCodeGenerator.isValidShortCode(null));
        assertFalse(shortCodeGenerator.isValidShortCode(""));
        assertFalse(shortCodeGenerator.isValidShortCode(" "));
        assertFalse(shortCodeGenerator.isValidShortCode("ab")); // too short
        assertFalse(shortCodeGenerator.isValidShortCode("abcdefghijk")); // too long
        assertFalse(shortCodeGenerator.isValidShortCode("abc-123")); // contains special character
        assertFalse(shortCodeGenerator.isValidShortCode("abc_123")); // contains underscore
        assertFalse(shortCodeGenerator.isValidShortCode("abc 123")); // contains space
    }
}