package com.aydog4nn.manitimleproje.service.abs;

import com.aydog4nn.manitimleproje.dto.room.CreateRoomRequest;
import com.aydog4nn.manitimleproje.dto.room.RoomResponse;
import com.aydog4nn.manitimleproje.dto.room.UpdateRoomRequest;
import java.util.List;
import java.util.UUID;

public interface RoomService {
    RoomResponse create(UUID currentUserId, CreateRoomRequest request);
    RoomResponse get(UUID currentUserId, UUID roomId);
    List<RoomResponse> list(UUID currentUserId);
    RoomResponse update(UUID currentUserId, UUID roomId, UpdateRoomRequest request);
    void delete(UUID currentUserId, UUID roomId);
}
