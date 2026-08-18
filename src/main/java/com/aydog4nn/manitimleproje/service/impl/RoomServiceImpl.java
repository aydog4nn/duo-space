package com.aydog4nn.manitimleproje.service.impl;

import com.aydog4nn.manitimleproje.exception.ResourceNotFoundException;
import com.aydog4nn.manitimleproje.exception.RoomJoinException;
import com.aydog4nn.manitimleproje.entity.User;
import com.aydog4nn.manitimleproje.repository.UserRepository;
import com.aydog4nn.manitimleproje.dto.room.CreateRoomRequest;
import com.aydog4nn.manitimleproje.dto.room.JoinRoomRequest;
import com.aydog4nn.manitimleproje.dto.room.RoomResponse;
import com.aydog4nn.manitimleproje.dto.room.UpdateRoomRequest;
import com.aydog4nn.manitimleproje.entity.Room;
import com.aydog4nn.manitimleproje.entity.RoomMember;
import com.aydog4nn.manitimleproje.repository.RoomMemberRepository;
import com.aydog4nn.manitimleproje.repository.RoomRepository;
import com.aydog4nn.manitimleproje.service.abs.RoomService;
import org.springframework.security.access.AccessDeniedException;
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
    @Override public RoomResponse create(UUID currentUserId, CreateRoomRequest request) {
        User owner = userRepository.findById(currentUserId).orElseThrow(() -> new ResourceNotFoundException("User", currentUserId));
        Room room = roomRepository.save(Room.create(request.name().trim(), UUID.randomUUID().toString().replace("-", "").substring(0, 12), owner));
        roomMemberRepository.save(RoomMember.owner(room, owner));
        return toResponse(room);
    }

    @Transactional
    @Override public RoomResponse join(UUID currentUserId, JoinRoomRequest request) {
        Room room = roomRepository.findByInviteCode(request.inviteCode().trim())
                .orElseThrow(() -> new RoomJoinException("Davet kodu geçersiz."));

        if (roomMemberRepository.existsByRoom_IdAndUser_Id(room.getId(), currentUserId)) {
            throw new RoomJoinException("Bu odaya zaten katıldın.");
        }
        if (roomMemberRepository.countByRoom_Id(room.getId()) >= 2) {
            throw new RoomJoinException("Bu oda dolu.");
        }

        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", currentUserId));
        roomMemberRepository.save(RoomMember.member(room, user));
        return toResponse(room);
    }

    @Override public RoomResponse get(UUID currentUserId, UUID roomId) {
        Room room = findRoom(roomId);
        requireMembership(room, currentUserId);
        return toResponse(room);
    }

    @Override public List<RoomResponse> list(UUID currentUserId) {
        return roomRepository.findAccessibleByUserId(currentUserId).stream().map(this::toResponse).toList();
    }

    @Transactional
    @Override public RoomResponse update(UUID currentUserId, UUID roomId, UpdateRoomRequest request) {
        Room room = findRoom(roomId);
        requireOwnership(room, currentUserId);
        room.rename(request.name().trim());
        return toResponse(room);
    }

    @Transactional
    @Override public void delete(UUID currentUserId, UUID roomId) {
        Room room = findRoom(roomId);
        requireOwnership(room, currentUserId);
        roomRepository.delete(room);
    }

    private Room findRoom(UUID id) { return roomRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Room", id)); }
    private void requireMembership(Room room, UUID userId) {
        if (!roomMemberRepository.existsByRoom_IdAndUser_Id(room.getId(), userId)) {
            throw new AccessDeniedException("Room membership is required");
        }
    }
    private void requireOwnership(Room room, UUID userId) {
        if (!room.getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("Room ownership is required");
        }
    }
    private RoomResponse toResponse(Room room) { return new RoomResponse(room.getId(), room.getName(), room.getInviteCode(), room.getOwner().getId(), room.getCreatedAt()); }
}
