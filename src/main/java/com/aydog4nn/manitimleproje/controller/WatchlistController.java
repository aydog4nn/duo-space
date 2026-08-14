package com.aydog4nn.manitimleproje.controller;

import com.aydog4nn.manitimleproje.dto.watchlist.CreateWatchlistItemRequest;
import com.aydog4nn.manitimleproje.dto.watchlist.UpdateWatchlistItemRequest;
import com.aydog4nn.manitimleproje.dto.watchlist.WatchlistItemResponse;
import com.aydog4nn.manitimleproje.service.abs.WatchlistService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rooms/{roomId}/watchlist")
public class WatchlistController {
    private final WatchlistService watchlistService;
    public WatchlistController(WatchlistService watchlistService) { this.watchlistService = watchlistService; }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public WatchlistItemResponse create(@PathVariable UUID roomId, @Valid @RequestBody CreateWatchlistItemRequest request) { return watchlistService.create(roomId, request); }
    @GetMapping public List<WatchlistItemResponse> list(@PathVariable UUID roomId) { return watchlistService.list(roomId); }
    @PutMapping("/{itemId}") public WatchlistItemResponse update(@PathVariable UUID itemId, @Valid @RequestBody UpdateWatchlistItemRequest request) { return watchlistService.update(itemId, request); }
    @DeleteMapping("/{itemId}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable UUID itemId) { watchlistService.delete(itemId); }
}
