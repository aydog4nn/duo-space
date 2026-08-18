package com.aydog4nn.manitimleproje.dto.room;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record JoinRoomRequest(@NotBlank @Size(max = 16) String inviteCode) {
}
