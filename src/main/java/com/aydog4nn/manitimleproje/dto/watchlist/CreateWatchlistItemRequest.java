package com.aydog4nn.manitimleproje.dto.watchlist;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record CreateWatchlistItemRequest(@NotNull UUID addedById, @NotBlank @Size(max = 255) String title, @Size(max = 2048) String sourceUrl) {}
