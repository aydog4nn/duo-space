package com.aydog4nn.manitimleproje.service.abs;

import com.aydog4nn.manitimleproje.dto.auth.RegisterRequest;
import com.aydog4nn.manitimleproje.dto.auth.RegisteredUserResponse;

public interface AuthService {
    RegisteredUserResponse register(RegisterRequest request);
}
