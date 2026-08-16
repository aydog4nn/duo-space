package com.aydog4nn.manitimleproje.controller;

import com.aydog4nn.manitimleproje.dto.room.CreateRoomRequest;
import com.aydog4nn.manitimleproje.dto.room.RoomResponse;
import com.aydog4nn.manitimleproje.dto.room.UpdateRoomRequest;
import com.aydog4nn.manitimleproje.service.abs.RoomService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rooms")
public class RoomController {
    private final RoomService roomService;
    public RoomController(RoomService roomService) { this.roomService = roomService; }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public RoomResponse create(Authentication authentication, @Valid @RequestBody CreateRoomRequest request) { return roomService.create(currentUserId(authentication), request); }
    @GetMapping("/{roomId}") public RoomResponse get(Authentication authentication, @PathVariable UUID roomId) { return roomService.get(currentUserId(authentication), roomId); }
    @GetMapping public List<RoomResponse> list(Authentication authentication) { return roomService.list(currentUserId(authentication)); }
    @PutMapping("/{roomId}") public RoomResponse update(Authentication authentication, @PathVariable UUID roomId, @Valid @RequestBody UpdateRoomRequest request) { return roomService.update(currentUserId(authentication), roomId, request); }
    @DeleteMapping("/{roomId}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(Authentication authentication, @PathVariable UUID roomId) { roomService.delete(currentUserId(authentication), roomId); }
    private UUID currentUserId(Authentication authentication) { return UUID.fromString(authentication.getName()); }
}
