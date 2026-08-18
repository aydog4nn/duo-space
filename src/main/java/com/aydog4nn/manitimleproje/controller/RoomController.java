package com.aydog4nn.manitimleproje.controller;

import com.aydog4nn.manitimleproje.dto.room.CreateRoomRequest;
import com.aydog4nn.manitimleproje.dto.room.JoinRoomRequest;
import com.aydog4nn.manitimleproje.dto.room.RoomResponse;
import com.aydog4nn.manitimleproje.dto.room.UpdateRoomRequest;
import com.aydog4nn.manitimleproje.service.abs.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rooms")
@Tag(name = "Oda İşlemleri", description = "Çiftlerin ortak alanı olan odaları yönetir. Oda oluşturmak, davet kodu ile odaya katılmak ve kendi odalarını görüntülemek için kullanılır.")
@SecurityRequirement(name = "bearerAuth")
public class RoomController {
    private final RoomService roomService;


    public RoomController(RoomService roomService) { this.roomService = roomService; }

    @PostMapping @ResponseStatus(HttpStatus.CREATED) @Operation(summary = "Yeni oda oluştur", description = "Giriş yapan kullanıcı odayı oluşturan kişi olur. Dönen davet kodu ikinci kullanıcıyla paylaşılır.") public RoomResponse create(Authentication authentication, @Valid @RequestBody CreateRoomRequest request) { return roomService.create(currentUserId(authentication), request); }

    @PostMapping("/join") @Operation(summary = "Davet koduyla odaya katıl", description = "Davet kodu doğruysa kullanıcı odaya eklenir. Bir odada en fazla iki kişi olabilir.") public RoomResponse join(Authentication authentication, @Valid @RequestBody JoinRoomRequest request) { return roomService.join(currentUserId(authentication), request); }

    @GetMapping("/{roomId}") @Operation(summary = "Oda bilgisi getir", description = "Sadece o odanın sahibi veya üyesi kendi odasının bilgilerini görebilir.") public RoomResponse get(Authentication authentication, @PathVariable UUID roomId) { return roomService.get(currentUserId(authentication), roomId); }

    @GetMapping @Operation(summary = "Odalarımı listele", description = "Giriş yapan kullanıcının üye olduğu bütün odaları listeler.") public List<RoomResponse> list(Authentication authentication) { return roomService.list(currentUserId(authentication)); }

    @PutMapping("/{roomId}") @Operation(summary = "Odayı güncelle", description = "Odanın ismini günceller. Bu işlemi sadece oda sahibi yapabilir.") public RoomResponse update(Authentication authentication, @PathVariable UUID roomId, @Valid @RequestBody UpdateRoomRequest request) { return roomService.update(currentUserId(authentication), roomId, request); }

    @DeleteMapping("/{roomId}") @ResponseStatus(HttpStatus.NO_CONTENT) @Operation(summary = "Odayı sil", description = "Odayı ve o odaya bağlı verileri siler. Sadece oda sahibinin yetkisi vardır.") public void delete(Authentication authentication, @PathVariable UUID roomId) { roomService.delete(currentUserId(authentication), roomId); }

    private UUID currentUserId(Authentication authentication) { return UUID.fromString(authentication.getName()); }
}
