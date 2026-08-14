package com.aydog4nn.manitimleproje.watchlist;

import com.aydog4nn.manitimleproje.config.ResourceNotFoundException;
import com.aydog4nn.manitimleproje.room.Room;
import com.aydog4nn.manitimleproje.room.RoomRepository;
import com.aydog4nn.manitimleproje.user.User;
import com.aydog4nn.manitimleproje.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class WatchlistService {
    private final WatchlistItemRepository itemRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    public WatchlistService(WatchlistItemRepository itemRepository, RoomRepository roomRepository, UserRepository userRepository) { this.itemRepository = itemRepository; this.roomRepository = roomRepository; this.userRepository = userRepository; }

    @Transactional
    public WatchlistItemResponse create(UUID roomId, CreateWatchlistItemRequest request) {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new ResourceNotFoundException("Room", roomId));
        User user = userRepository.findById(request.addedById()).orElseThrow(() -> new ResourceNotFoundException("User", request.addedById()));
        return toResponse(itemRepository.save(WatchlistItem.create(room, user, request.title().trim(), request.sourceUrl())));
    }
    public List<WatchlistItemResponse> list(UUID roomId) { return itemRepository.findByRoom_IdOrderByCreatedAtDesc(roomId).stream().map(this::toResponse).toList(); }
    @Transactional
    public WatchlistItemResponse update(UUID itemId, UpdateWatchlistItemRequest request) { WatchlistItem item = findItem(itemId); item.update(request.title().trim(), request.sourceUrl(), request.status()); return toResponse(item); }
    @Transactional
    public void delete(UUID itemId) { itemRepository.delete(findItem(itemId)); }
    private WatchlistItem findItem(UUID id) { return itemRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Watchlist item", id)); }
    private WatchlistItemResponse toResponse(WatchlistItem item) { return new WatchlistItemResponse(item.getId(), item.getTitle(), item.getSourceUrl(), item.getStatus(), item.getCreatedAt()); }
}
