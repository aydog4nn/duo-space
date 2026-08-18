package com.aydog4nn.manitimleproje.service.impl;

import com.aydog4nn.manitimleproje.dto.room.JoinRoomRequest;
import com.aydog4nn.manitimleproje.entity.Room;
import com.aydog4nn.manitimleproje.entity.User;
import com.aydog4nn.manitimleproje.exception.RoomJoinException;
import com.aydog4nn.manitimleproje.repository.RoomMemberRepository;
import com.aydog4nn.manitimleproje.repository.RoomRepository;
import com.aydog4nn.manitimleproje.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceImplTest {

    @Mock private RoomRepository roomRepository;
    @Mock private RoomMemberRepository roomMemberRepository;
    @Mock private UserRepository userRepository;
    @InjectMocks private RoomServiceImpl roomService;

    @Test
    void shouldAddUserToRoomWhenInviteCodeIsValidAndRoomHasOneMember() {
        UUID roomId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        Room room = mock(Room.class);
        User user = mock(User.class);
        User owner = mock(User.class);

        when(roomRepository.findByInviteCode("invite-123")).thenReturn(Optional.of(room));
        when(room.getId()).thenReturn(roomId);
        when(roomMemberRepository.existsByRoom_IdAndUser_Id(roomId, userId)).thenReturn(false);
        when(roomMemberRepository.countByRoom_Id(roomId)).thenReturn(1L);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(room.getOwner()).thenReturn(owner);
        when(owner.getId()).thenReturn(ownerId);

        roomService.join(userId, new JoinRoomRequest(" invite-123 "));

        verify(roomMemberRepository).save(any());
    }

    @Test
    void shouldRejectJoinWhenUserIsAlreadyAMember() {
        UUID roomId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Room room = mock(Room.class);

        when(roomRepository.findByInviteCode("invite-123")).thenReturn(Optional.of(room));
        when(room.getId()).thenReturn(roomId);
        when(roomMemberRepository.existsByRoom_IdAndUser_Id(roomId, userId)).thenReturn(true);

        assertThrows(RoomJoinException.class, () -> roomService.join(userId, new JoinRoomRequest("invite-123")));
        verify(roomMemberRepository, never()).save(any());
    }

    @Test
    void shouldRejectJoinWhenRoomAlreadyHasTwoMembers() {
        UUID roomId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Room room = mock(Room.class);

        when(roomRepository.findByInviteCode("invite-123")).thenReturn(Optional.of(room));
        when(room.getId()).thenReturn(roomId);
        when(roomMemberRepository.existsByRoom_IdAndUser_Id(roomId, userId)).thenReturn(false);
        when(roomMemberRepository.countByRoom_Id(roomId)).thenReturn(2L);

        assertThrows(RoomJoinException.class, () -> roomService.join(userId, new JoinRoomRequest("invite-123")));
        verify(roomMemberRepository, never()).save(any());
    }
}
