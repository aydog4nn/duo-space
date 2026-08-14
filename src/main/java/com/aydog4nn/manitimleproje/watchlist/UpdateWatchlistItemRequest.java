package com.aydog4nn.manitimleproje.watchlist;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateWatchlistItemRequest(@NotBlank @Size(max = 255) String title, @Size(max = 2048) String sourceUrl, @NotNull WatchlistStatus status) {}
