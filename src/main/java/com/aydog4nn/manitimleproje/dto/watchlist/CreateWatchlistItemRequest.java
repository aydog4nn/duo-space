package com.aydog4nn.manitimleproje.dto.watchlist;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateWatchlistItemRequest(@NotBlank @Size(max = 255) String title, @Size(max = 2048) String sourceUrl) {}
