package com.aydog4nn.manitimleproje.room;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rooms")
public class RoomController {
    private final RoomService roomService;
    public RoomController(RoomService roomService) { this.roomService = roomService; }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public RoomResponse create(@Valid @RequestBody CreateRoomRequest request) { return roomService.create(request); }
    @GetMapping("/{roomId}") public RoomResponse get(@PathVariable UUID roomId) { return roomService.get(roomId); }
    @GetMapping public List<RoomResponse> list(@RequestParam UUID ownerId) { return roomService.listByOwner(ownerId); }
    @PutMapping("/{roomId}") public RoomResponse update(@PathVariable UUID roomId, @Valid @RequestBody UpdateRoomRequest request) { return roomService.update(roomId, request); }
    @DeleteMapping("/{roomId}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable UUID roomId) { roomService.delete(roomId); }
}
