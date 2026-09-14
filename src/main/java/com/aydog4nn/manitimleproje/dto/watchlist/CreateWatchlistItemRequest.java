package com.aydog4nn.manitimleproje.dto.watchlist;

import com.aydog4nn.manitimleproje.common.validation.WebLink;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateWatchlistItemRequest(@NotBlank @Size(max = 255) String title,
                                        @WebLink @Size(max = 2048) String sourceUrl) {}
