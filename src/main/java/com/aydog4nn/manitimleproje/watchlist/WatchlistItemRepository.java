package com.aydog4nn.manitimleproje.watchlist;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface WatchlistItemRepository extends JpaRepository<WatchlistItem, UUID> {
    List<WatchlistItem> findByRoom_IdOrderByCreatedAtDesc(UUID roomId);
}
