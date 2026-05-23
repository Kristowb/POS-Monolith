package com.app.service;

import com.app.config.JwtService;
import com.app.dto.AuthResponse;
import com.app.dto.LoginRequest;
import com.app.dto.RegisterRequest;
import com.app.dto.UserResponse;
import com.app.model.InvalidatedToken;
import com.app.model.UserEntity;
import com.app.repository.InvalidatedTokenRepository;
import com.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import java.net.URI;
import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final InvalidatedTokenRepository invalidatedTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw Problem.builder()
                    .withType(URI.create("https://api.app.com/errors/duplicate-username"))
                    .withTitle("Username Duplikat")
                    .withStatus(Status.CONFLICT)
                    .withDetail("Username '" + request.getUsername() + "' sudah digunakan oleh pengguna lain.")
                    .build();
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw Problem.builder()
                    .withType(URI.create("https://api.app.com/errors/duplicate-email"))
                    .withTitle("Email Duplikat")
                    .withStatus(Status.CONFLICT)
                    .withDetail("Email '" + request.getEmail() + "' sudah digunakan oleh pengguna lain.")
                    .build();
        }

        UserEntity user = UserEntity.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .fullName(request.getFullName())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .active(true)
                .build();

        UserEntity savedUser = userRepository.save(user);
        
        String token = jwtService.generateToken(savedUser.getUsername(), savedUser.getRole());
        
        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(jwtService.getExpirationInSeconds())
                .user(mapToUserResponse(savedUser))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        // Cari pengguna berdasarkan username atau email
        Optional<UserEntity> userOpt = userRepository.findByUsername(request.getUsernameOrEmail());
        if (userOpt.isEmpty()) {
            // Jika username tidak ditemukan, coba cari berdasarkan email
            userOpt = userRepository.findAll().stream()
                    .filter(u -> u.getEmail().equalsIgnoreCase(request.getUsernameOrEmail()))
                    .findFirst();
        }

        if (userOpt.isEmpty()) {
            throw Problem.builder()
                    .withType(URI.create("https://api.app.com/errors/bad-credentials"))
                    .withTitle("Kredensial Tidak Valid")
                    .withStatus(Status.UNAUTHORIZED)
                    .withDetail("Username/email atau password salah.")
                    .build();
        }

        UserEntity user = userOpt.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw Problem.builder()
                    .withType(URI.create("https://api.app.com/errors/bad-credentials"))
                    .withTitle("Kredensial Tidak Valid")
                    .withStatus(Status.UNAUTHORIZED)
                    .withDetail("Username/email atau password salah.")
                    .build();
        }

        if (!user.isActive()) {
            throw Problem.builder()
                    .withType(URI.create("https://api.app.com/errors/user-disabled"))
                    .withTitle("Akun Dinonaktifkan")
                    .withStatus(Status.FORBIDDEN)
                    .withDetail("Akun pengguna ini telah dinonaktifkan.")
                    .build();
        }

        String token = jwtService.generateToken(user.getUsername(), user.getRole());

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(jwtService.getExpirationInSeconds())
                .user(mapToUserResponse(user))
                .build();
    }

    @Override
    @Transactional
    public void logout(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw Problem.builder()
                    .withType(URI.create("https://api.app.com/errors/bad-request"))
                    .withTitle("Format Authorization Tidak Valid")
                    .withStatus(Status.BAD_REQUEST)
                    .withDetail("Header Authorization harus menyertakan token Bearer.")
                    .build();
        }

        String token = authHeader.substring(7);

        // Jika token sudah ter-blacklist, abaikan (idempotent logout)
        if (invalidatedTokenRepository.existsByToken(token)) {
            return;
        }

        try {
            Instant expiryDate = jwtService.extractExpiration(token);
            InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                    .token(token)
                    .expiryDate(expiryDate)
                    .build();

            invalidatedTokenRepository.save(invalidatedToken);
        } catch (Exception e) {
            throw Problem.builder()
                    .withType(URI.create("https://api.app.com/errors/invalid-token"))
                    .withTitle("Token Tidak Valid")
                    .withStatus(Status.BAD_REQUEST)
                    .withDetail("Token JWT sudah kedaluwarsa atau tidak valid.")
                    .build();
        }
    }

    private UserResponse mapToUserResponse(UserEntity entity) {
        return UserResponse.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .fullName(entity.getFullName())
                .role(entity.getRole())
                .active(entity.isActive())
                .build();
    }
}
