package com.aydog4nn.manitimleproje.service.impl;

import com.aydog4nn.manitimleproje.exception.ResourceNotFoundException;
import com.aydog4nn.manitimleproje.entity.Room;
import com.aydog4nn.manitimleproje.repository.RoomRepository;
import com.aydog4nn.manitimleproje.entity.User;
import com.aydog4nn.manitimleproje.repository.UserRepository;
import com.aydog4nn.manitimleproje.dto.watchlist.CreateWatchlistItemRequest;
import com.aydog4nn.manitimleproje.dto.watchlist.UpdateWatchlistItemRequest;
import com.aydog4nn.manitimleproje.dto.watchlist.WatchlistItemResponse;
import com.aydog4nn.manitimleproje.entity.WatchlistItem;
import com.aydog4nn.manitimleproje.repository.WatchlistItemRepository;
import com.aydog4nn.manitimleproje.service.abs.WatchlistService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class WatchlistServiceImpl implements WatchlistService {
    private final WatchlistItemRepository itemRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    public WatchlistServiceImpl(WatchlistItemRepository itemRepository, RoomRepository roomRepository, UserRepository userRepository) { this.itemRepository = itemRepository; this.roomRepository = roomRepository; this.userRepository = userRepository; }

    @Transactional
    @Override public WatchlistItemResponse create(UUID roomId, CreateWatchlistItemRequest request) {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new ResourceNotFoundException("Room", roomId));
        User user = userRepository.findById(request.addedById()).orElseThrow(() -> new ResourceNotFoundException("User", request.addedById()));
        return toResponse(itemRepository.save(WatchlistItem.create(room, user, request.title().trim(), request.sourceUrl())));
    }
    @Override public List<WatchlistItemResponse> list(UUID roomId) { return itemRepository.findByRoom_IdOrderByCreatedAtDesc(roomId).stream().map(this::toResponse).toList(); }
    @Transactional
    @Override public WatchlistItemResponse update(UUID itemId, UpdateWatchlistItemRequest request) { WatchlistItem item = findItem(itemId); item.update(request.title().trim(), request.sourceUrl(), request.status()); return toResponse(item); }
    @Transactional
    @Override public void delete(UUID itemId) { itemRepository.delete(findItem(itemId)); }
    private WatchlistItem findItem(UUID id) { return itemRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Watchlist item", id)); }
    private WatchlistItemResponse toResponse(WatchlistItem item) { return new WatchlistItemResponse(item.getId(), item.getTitle(), item.getSourceUrl(), item.getStatus(), item.getCreatedAt()); }
}
