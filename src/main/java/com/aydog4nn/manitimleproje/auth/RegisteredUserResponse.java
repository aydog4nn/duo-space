package com.aydog4nn.manitimleproje.auth;

import java.util.UUID;

public record RegisteredUserResponse(UUID id, String username, String email) {
}
