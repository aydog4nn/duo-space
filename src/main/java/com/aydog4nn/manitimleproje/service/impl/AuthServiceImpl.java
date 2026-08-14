package com.aydog4nn.manitimleproje.service.impl;

import com.aydog4nn.manitimleproje.dto.auth.RegisterRequest;
import com.aydog4nn.manitimleproje.dto.auth.RegisteredUserResponse;
import com.aydog4nn.manitimleproje.entity.User;
import com.aydog4nn.manitimleproje.exception.DuplicateUserException;
import com.aydog4nn.manitimleproje.repository.UserRepository;
import com.aydog4nn.manitimleproje.service.abs.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @Override
    public RegisteredUserResponse register(RegisterRequest request) {
        String username = request.username().trim();
        String email = request.email().trim().toLowerCase(Locale.ROOT);

        if (userRepository.existsByUsername(username)) {
            throw new DuplicateUserException("username");
        }
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateUserException("email");
        }

        User user = User.create(username, email, passwordEncoder.encode(request.password()));
        User savedUser = userRepository.save(user);
        return new RegisteredUserResponse(savedUser.getId(), savedUser.getUsername(), savedUser.getEmail());
    }
}
