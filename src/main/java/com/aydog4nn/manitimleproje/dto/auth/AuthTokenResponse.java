package com.aydog4nn.manitimleproje.dto.auth;

public record AuthTokenResponse(String accessToken, String tokenType, long expiresIn) {
}
