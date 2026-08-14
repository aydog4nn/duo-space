package com.aydog4nn.manitimleproje.service.abs;

import com.aydog4nn.manitimleproje.dto.room.CreateRoomRequest;
import com.aydog4nn.manitimleproje.dto.room.RoomResponse;
import com.aydog4nn.manitimleproje.dto.room.UpdateRoomRequest;
import java.util.List;
import java.util.UUID;

public interface RoomService {
    RoomResponse create(CreateRoomRequest request);
    RoomResponse get(UUID roomId);
    List<RoomResponse> listByOwner(UUID ownerId);
    RoomResponse update(UUID roomId, UpdateRoomRequest request);
    void delete(UUID roomId);
}
