package com.aydog4nn.manitimleproje.config;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JwtServiceTest {

    private static final String SECRET = "ZHVvLXNwYWNlLWxvY2FsLWRldmVsb3BtZW50LWp3dC1zZWNyZXQta2V5LWNoYW5nZS1iZWZvcmUtcHJvZHVjdGlvbg==";

    @Test
    void shouldExtractTheSameUserIdFromAGeneratedAccessToken() {
        JwtService jwtService = new JwtService(SECRET, Duration.ofMinutes(15));
        UUID userId = UUID.randomUUID();

        String token = jwtService.generateAccessToken(userId, "akin");

        assertEquals(userId, jwtService.extractUserId(token));
    }
}
