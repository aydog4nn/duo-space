package com.aydog4nn.manitimleproje.service.impl;

import com.aydog4nn.manitimleproje.exception.ResourceNotFoundException;
import com.aydog4nn.manitimleproje.entity.User;
import com.aydog4nn.manitimleproje.repository.UserRepository;
import com.aydog4nn.manitimleproje.dto.room.CreateRoomRequest;
import com.aydog4nn.manitimleproje.dto.room.RoomResponse;
import com.aydog4nn.manitimleproje.dto.room.UpdateRoomRequest;
import com.aydog4nn.manitimleproje.entity.Room;
import com.aydog4nn.manitimleproje.entity.RoomMember;
import com.aydog4nn.manitimleproje.repository.RoomMemberRepository;
import com.aydog4nn.manitimleproje.repository.RoomRepository;
import com.aydog4nn.manitimleproje.service.abs.RoomService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;
    private final RoomMemberRepository roomMemberRepository;
    private final UserRepository userRepository;

    public RoomServiceImpl(RoomRepository roomRepository, RoomMemberRepository roomMemberRepository, UserRepository userRepository) {
        this.roomRepository = roomRepository;
        this.roomMemberRepository = roomMemberRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    @Override public RoomResponse create(CreateRoomRequest request) {
        User owner = userRepository.findById(request.ownerId()).orElseThrow(() -> new ResourceNotFoundException("User", request.ownerId()));
        Room room = roomRepository.save(Room.create(request.name().trim(), UUID.randomUUID().toString().replace("-", "").substring(0, 12), owner));
        roomMemberRepository.save(RoomMember.owner(room, owner));
        return toResponse(room);
    }

    @Override public RoomResponse get(UUID roomId) { return toResponse(findRoom(roomId)); }
    @Override public List<RoomResponse> listByOwner(UUID ownerId) { return roomRepository.findByOwner_Id(ownerId).stream().map(this::toResponse).toList(); }

    @Transactional
    @Override public RoomResponse update(UUID roomId, UpdateRoomRequest request) {
        Room room = findRoom(roomId);
        room.rename(request.name().trim());
        return toResponse(room);
    }

    @Transactional
    @Override public void delete(UUID roomId) { roomRepository.delete(findRoom(roomId)); }

    private Room findRoom(UUID id) { return roomRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Room", id)); }
    private RoomResponse toResponse(Room room) { return new RoomResponse(room.getId(), room.getName(), room.getInviteCode(), room.getOwner().getId(), room.getCreatedAt()); }
}
