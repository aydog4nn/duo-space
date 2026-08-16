package com.aydog4nn.manitimleproje.service.impl;

import com.aydog4nn.manitimleproje.exception.ResourceNotFoundException;
import com.aydog4nn.manitimleproje.entity.Room;
import com.aydog4nn.manitimleproje.repository.RoomRepository;
import com.aydog4nn.manitimleproje.repository.RoomMemberRepository;
import com.aydog4nn.manitimleproje.entity.User;
import com.aydog4nn.manitimleproje.repository.UserRepository;
import com.aydog4nn.manitimleproje.dto.watchlist.CreateWatchlistItemRequest;
import com.aydog4nn.manitimleproje.dto.watchlist.UpdateWatchlistItemRequest;
import com.aydog4nn.manitimleproje.dto.watchlist.WatchlistItemResponse;
import com.aydog4nn.manitimleproje.entity.WatchlistItem;
import com.aydog4nn.manitimleproje.repository.WatchlistItemRepository;
import com.aydog4nn.manitimleproje.service.abs.WatchlistService;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class WatchlistServiceImpl implements WatchlistService {
    private final WatchlistItemRepository itemRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final RoomMemberRepository roomMemberRepository;
    public WatchlistServiceImpl(WatchlistItemRepository itemRepository, RoomRepository roomRepository, UserRepository userRepository, RoomMemberRepository roomMemberRepository) { this.itemRepository = itemRepository; this.roomRepository = roomRepository; this.userRepository = userRepository; this.roomMemberRepository = roomMemberRepository; }

    @Transactional
    @Override public WatchlistItemResponse create(UUID currentUserId, UUID roomId, CreateWatchlistItemRequest request) {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new ResourceNotFoundException("Room", roomId));
        requireMembership(roomId, currentUserId);
        User user = userRepository.findById(currentUserId).orElseThrow(() -> new ResourceNotFoundException("User", currentUserId));
        return toResponse(itemRepository.save(WatchlistItem.create(room, user, request.title().trim(), request.sourceUrl())));
    }
    @Override public List<WatchlistItemResponse> list(UUID currentUserId, UUID roomId) {
        roomRepository.findById(roomId).orElseThrow(() -> new ResourceNotFoundException("Room", roomId));
        requireMembership(roomId, currentUserId);
        return itemRepository.findByRoom_IdOrderByCreatedAtDesc(roomId).stream().map(this::toResponse).toList();
    }
    @Transactional
    @Override public WatchlistItemResponse update(UUID currentUserId, UUID itemId, UpdateWatchlistItemRequest request) {
        WatchlistItem item = findItem(itemId);
        requireMembership(item.getRoom().getId(), currentUserId);
        item.update(request.title().trim(), request.sourceUrl(), request.status());
        return toResponse(item);
    }
    @Transactional
    @Override public void delete(UUID currentUserId, UUID itemId) {
        WatchlistItem item = findItem(itemId);
        requireMembership(item.getRoom().getId(), currentUserId);
        itemRepository.delete(item);
    }
    private WatchlistItem findItem(UUID id) { return itemRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Watchlist item", id)); }
    private void requireMembership(UUID roomId, UUID userId) {
        if (!roomMemberRepository.existsByRoom_IdAndUser_Id(roomId, userId)) {
            throw new AccessDeniedException("Room membership is required");
        }
    }
    private WatchlistItemResponse toResponse(WatchlistItem item) { return new WatchlistItemResponse(item.getId(), item.getTitle(), item.getSourceUrl(), item.getStatus(), item.getCreatedAt()); }
}
