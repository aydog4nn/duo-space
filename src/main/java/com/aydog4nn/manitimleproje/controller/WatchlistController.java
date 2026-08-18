package com.aydog4nn.manitimleproje.controller;

import com.aydog4nn.manitimleproje.dto.watchlist.CreateWatchlistItemRequest;
import com.aydog4nn.manitimleproje.dto.watchlist.UpdateWatchlistItemRequest;
import com.aydog4nn.manitimleproje.dto.watchlist.WatchlistItemResponse;
import com.aydog4nn.manitimleproje.service.abs.WatchlistService;
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
@RequestMapping("/api/v1/rooms/{roomId}/watchlist")
@Tag(name = "Ortak Liste İşlemleri", description = "Odadaki izlenecek film veya oynanacak oyun listesini yönetir. Liste iki kullanıcı için ortaktır; bu yüzden işlemler oda üyeliği kontrol edilerek yapılır.")
@SecurityRequirement(name = "bearerAuth")
public class WatchlistController {
    private final WatchlistService watchlistService;
    public WatchlistController(WatchlistService watchlistService) { this.watchlistService = watchlistService; }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) @Operation(summary = "Listeye seçim ekle", description = "Film, oyun veya başka bir ortak seçim ekler. Kaydı ekleyen kullanıcı token içinden bulunur.") public WatchlistItemResponse create(Authentication authentication, @PathVariable UUID roomId, @Valid @RequestBody CreateWatchlistItemRequest request) { return watchlistService.create(currentUserId(authentication), roomId, request); }
    @GetMapping @Operation(summary = "Ortak listeyi getir", description = "Odadaki bütün seçimleri getirir. Sadece odaya üye kullanıcılar görebilir.") public List<WatchlistItemResponse> list(Authentication authentication, @PathVariable UUID roomId) { return watchlistService.list(currentUserId(authentication), roomId); }
    @PutMapping("/{itemId}") @Operation(summary = "Liste seçimini güncelle", description = "Başlığı, bağlantıyı veya izleme durumunu değiştirir. Örneğin PLANNED durumunu WATCHING yapabilirsin.") public WatchlistItemResponse update(Authentication authentication, @PathVariable UUID itemId, @Valid @RequestBody UpdateWatchlistItemRequest request) { return watchlistService.update(currentUserId(authentication), itemId, request); }
    @DeleteMapping("/{itemId}") @ResponseStatus(HttpStatus.NO_CONTENT) @Operation(summary = "Listeden seçim sil", description = "Seçilen film veya oyunu ortak listeden kaldırır.") public void delete(Authentication authentication, @PathVariable UUID itemId) { watchlistService.delete(currentUserId(authentication), itemId); }
    private UUID currentUserId(Authentication authentication) { return UUID.fromString(authentication.getName()); }
}
