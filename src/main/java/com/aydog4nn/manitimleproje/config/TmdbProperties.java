package com.aydog4nn.manitimleproje.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "tmdb")
public record TmdbProperties(String apiReadAccessToken, String baseUrl, String imageBaseUrl, String language) {
}
