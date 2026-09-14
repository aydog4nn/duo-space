package com.aydog4nn.manitimleproje.controller;

import com.aydog4nn.manitimleproje.entity.Room;
import com.aydog4nn.manitimleproje.entity.User;
import com.aydog4nn.manitimleproje.entity.WatchlistItem;
import com.aydog4nn.manitimleproje.entity.enums.WatchlistStatus;
import com.aydog4nn.manitimleproje.exception.GlobalExceptionHandler;
import com.aydog4nn.manitimleproje.repository.RoomMemberRepository;
import com.aydog4nn.manitimleproje.repository.RoomRepository;
import com.aydog4nn.manitimleproje.repository.UserRepository;
import com.aydog4nn.manitimleproje.repository.WatchlistItemRepository;
import com.aydog4nn.manitimleproje.service.impl.WatchlistServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Controller, servis ve hata eşlemesi birlikte çalışır; repository ve kimlik test verisidir.
class WatchlistControllerTest {
    private final WatchlistItemRepository items = mock(WatchlistItemRepository.class);
    private final RoomMemberRepository members = mock(RoomMemberRepository.class);
    private final UUID userId = UUID.randomUUID();
    private final UUID roomId = UUID.randomUUID();
    private final UUID itemId = UUID.randomUUID();
    private WatchlistItem item;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        User user = User.create("test-user", "user@example.test", "test-only-hash");
        Room room = Room.create("Test odası", "test-invite", user);
        ReflectionTestUtils.setField(room, "id", roomId);
        item = WatchlistItem.create(room, user, "Eski başlık", null);
        ReflectionTestUtils.setField(item, "id", itemId);
        when(items.findById(itemId)).thenReturn(Optional.of(item));
        when(members.existsByRoom_IdAndUser_Id(roomId, userId)).thenReturn(true);

        WatchlistServiceImpl service = new WatchlistServiceImpl(items,
                mock(RoomRepository.class), mock(UserRepository.class), members);
        mvc = MockMvcBuilders.standaloneSetup(new WatchlistController(service))
                .setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    @ParameterizedTest
    @ValueSource(strings = {"PUT", "DELETE"})
    void shouldAllowMemberToChangeAnItemInTheSameRoom(String method) throws Exception {
        perform(method, roomId, userId, itemId, method.equals("PUT") ? 200 : 204);

        if (method.equals("PUT")) {
            assertEquals("Yeni başlık", item.getTitle());
            assertEquals(WatchlistStatus.WATCHING, item.getStatus());
        } else {
            verify(items).delete(item);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"PUT", "DELETE"})
    void shouldRejectMismatchedRoomEvenWhenUserBelongsToBothRooms(String method) throws Exception {
        UUID otherRoomId = UUID.randomUUID();
        when(members.existsByRoom_IdAndUser_Id(otherRoomId, userId)).thenReturn(true);

        perform(method, otherRoomId, userId, itemId, 404);

        assertItemUnchanged();
    }

    @ParameterizedTest
    @ValueSource(strings = {"PUT", "DELETE"})
    void shouldRejectAnotherUserOutsideTheRoom(String method) throws Exception {
        perform(method, roomId, UUID.randomUUID(), itemId, 403);

        assertItemUnchanged();
    }

    @ParameterizedTest
    @ValueSource(strings = {"PUT", "DELETE"})
    void shouldRejectAnUnknownItem(String method) throws Exception {
        perform(method, roomId, userId, UUID.randomUUID(), 404);

        assertItemUnchanged();
    }

    private void perform(String method, UUID requestedRoom, UUID requestedUser,
                         UUID requestedItem, int expectedStatus) throws Exception {
        mvc.perform(request(HttpMethod.valueOf(method),
                        "/api/v1/rooms/{roomId}/watchlist/{itemId}", requestedRoom, requestedItem)
                        .principal(new UsernamePasswordAuthenticationToken(
                                requestedUser.toString(), null, List.of()))
                        .contentType("application/json")
                        .content("""
                                {"title":"Yeni başlık","sourceUrl":null,"status":"WATCHING"}
                                """))
                .andExpect(status().is(expectedStatus));
    }

    @ParameterizedTest
    @ValueSource(strings = {"POST", "PUT"})
    void shouldRejectNonWebLinksBeforeCallingTheService(String method) throws Exception {
        String path = "/api/v1/rooms/" + roomId + "/watchlist";
        if (method.equals("PUT")) {
            path += "/" + itemId;
        }
        mvc.perform(request(HttpMethod.valueOf(method), path)
                        .principal(new UsernamePasswordAuthenticationToken(userId.toString(), null, List.of()))
                        .contentType("application/json")
                        .content("""
                                {"title":"Film","sourceUrl":"javascript:alert(1)","status":"PLANNED"}
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(items, members);
        assertItemUnchanged();
    }

    private void assertItemUnchanged() {
        assertEquals("Eski başlık", item.getTitle());
        assertEquals(WatchlistStatus.PLANNED, item.getStatus());
        verify(items, never()).delete(any());
        verify(items, never()).save(any());
    }
}
