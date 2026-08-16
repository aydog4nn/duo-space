package com.aydog4nn.manitimleproje.controller;

import com.aydog4nn.manitimleproje.dto.auth.AuthTokenResponse;
import com.aydog4nn.manitimleproje.dto.auth.LoginRequest;
import com.aydog4nn.manitimleproje.dto.auth.RegisterRequest;
import com.aydog4nn.manitimleproje.dto.auth.RegisteredUserResponse;
import com.aydog4nn.manitimleproje.service.abs.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisteredUserResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthTokenResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
