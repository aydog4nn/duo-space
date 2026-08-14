package com.aydog4nn.manitimleproje.room;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateRoomRequest(@NotBlank @Size(max = 100) String name) {}
