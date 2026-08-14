package com.aydog4nn.manitimleproje.watchlist;

import java.time.Instant;
import java.util.UUID;

public record WatchlistItemResponse(UUID id, String title, String sourceUrl, WatchlistStatus status, Instant createdAt) {}
