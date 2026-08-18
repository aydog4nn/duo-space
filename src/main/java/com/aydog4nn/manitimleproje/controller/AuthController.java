package com.aydog4nn.manitimleproje.controller;

import com.aydog4nn.manitimleproje.dto.auth.AuthTokenResponse;
import com.aydog4nn.manitimleproje.dto.auth.LoginRequest;
import com.aydog4nn.manitimleproje.dto.auth.RegisterRequest;
import com.aydog4nn.manitimleproje.dto.auth.RegisteredUserResponse;
import com.aydog4nn.manitimleproje.service.abs.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Kullanıcı İşlemleri", description = "Bu controller kayıt olma ve giriş yapma işlemlerini tutuyor. Giriş yaptıktan sonra gelen JWT token, diğer korumalı endpointlerde kullanılıyor.")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Kullanıcı kaydı", description = "Yeni bir kullanıcı oluşturur. Şifre veritabanına düz metin olarak değil, şifrelenmiş şekilde kaydedilir.")
    public RegisteredUserResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    @Operation(summary = "Giriş yap", description = "E-posta ve şifre doğruysa JWT access token döner. Bu tokeni Swagger'da Authorize butonundan ekleyebilirsin.")
    public AuthTokenResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
