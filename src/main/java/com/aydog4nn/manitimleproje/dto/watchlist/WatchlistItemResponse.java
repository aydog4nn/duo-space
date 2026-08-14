package com.aydog4nn.manitimleproje.dto.watchlist;

import com.aydog4nn.manitimleproje.entity.enums.WatchlistStatus;
import java.time.Instant;
import java.util.UUID;

public record WatchlistItemResponse(UUID id, String title, String sourceUrl, WatchlistStatus status, Instant createdAt) {}
