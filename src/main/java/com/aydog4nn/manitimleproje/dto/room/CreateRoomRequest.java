package com.aydog4nn.manitimleproje.dto.room;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record CreateRoomRequest(@NotNull UUID ownerId, @NotBlank @Size(max = 100) String name) {}
