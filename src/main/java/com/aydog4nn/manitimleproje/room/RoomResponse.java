package com.aydog4nn.manitimleproje.room;

import java.time.Instant;
import java.util.UUID;

public record RoomResponse(UUID id, String name, String inviteCode, UUID ownerId, Instant createdAt) {}
