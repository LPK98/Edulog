package com.edulog.service;

import com.edulog.dto.LoginRequest;
import com.edulog.dto.LoginResponse;
import com.edulog.model.Role;
import com.edulog.model.User;
import com.edulog.repository.UserRepository;
import com.edulog.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationManager authenticationManager, UserRepository userRepository,
                       JwtTokenProvider tokenProvider, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.tokenProvider = tokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = tokenProvider.generateToken(user.getUsername(), user.getRole().name());

        Long studentId = user.getStudent() != null ? user.getStudent().getId() : null;

        return new LoginResponse(token, user.getId(), user.getName(), user.getRole().name(),
                user.getUsername(), studentId);
    }

    public User register(String username, String password, String name, String email, Role role) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }
        User user = new User(username, passwordEncoder.encode(password), name, email, role);
        return userRepository.save(user);
    }
}
