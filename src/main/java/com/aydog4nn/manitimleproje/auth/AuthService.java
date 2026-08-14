package com.aydog4nn.manitimleproje.auth;

import com.aydog4nn.manitimleproje.user.User;
import com.aydog4nn.manitimleproje.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
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
