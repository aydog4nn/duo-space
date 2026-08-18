package com.aydog4nn.manitimleproje.dto.movie;

public record MovieSearchResponse(Long tmdbId, String title, String releaseYear, String posterUrl, String overview, Double voteAverage) {
}
