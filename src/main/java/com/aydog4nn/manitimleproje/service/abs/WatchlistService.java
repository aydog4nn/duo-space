package com.aydog4nn.manitimleproje.service.abs;

import com.aydog4nn.manitimleproje.dto.watchlist.CreateWatchlistItemRequest;
import com.aydog4nn.manitimleproje.dto.watchlist.UpdateWatchlistItemRequest;
import com.aydog4nn.manitimleproje.dto.watchlist.WatchlistItemResponse;
import java.util.List;
import java.util.UUID;

public interface WatchlistService {
    WatchlistItemResponse create(UUID currentUserId, UUID roomId, CreateWatchlistItemRequest request);
    List<WatchlistItemResponse> list(UUID currentUserId, UUID roomId);
    WatchlistItemResponse update(UUID currentUserId, UUID itemId, UpdateWatchlistItemRequest request);
    void delete(UUID currentUserId, UUID itemId);
}
