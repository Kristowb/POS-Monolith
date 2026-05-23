package com.app.service;

import com.app.config.JwtService;
import com.app.dto.AuthResponse;
import com.app.dto.GoogleLoginRequest;
import com.app.dto.LoginRequest;
import com.app.dto.RegisterRequest;
import com.app.dto.UserResponse;
import com.app.model.InvalidatedToken;
import com.app.model.RoleEntity;
import com.app.model.UserEntity;
import com.app.repository.InvalidatedTokenRepository;
import com.app.repository.RoleRepository;
import com.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
    private final RoleRepository roleRepository;

    @Value("${app.google.client-id:}")
    private String googleClientId;

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

        RoleEntity userRole = roleRepository.findByCode(request.getRole())
                .orElseGet(() -> roleRepository.findByCode("ROLE_KNIGHT")
                        .orElseThrow(() -> Problem.builder()
                                .withType(URI.create("https://api.app.com/errors/role-not-found"))
                                .withTitle("Role Tidak Ditemukan")
                                .withStatus(Status.INTERNAL_SERVER_ERROR)
                                .withDetail("Role default ROLE_KNIGHT tidak ditemukan di database.")
                                .build()));

        UserEntity user = UserEntity.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .fullName(request.getFullName())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(java.util.Set.of(userRole))
                .level(1)
                .experience(0)
                .active(true)
                .build();

        UserEntity savedUser = userRepository.save(user);
        
        String rolesStr = savedUser.getRoles().stream()
                .map(RoleEntity::getCode)
                .collect(java.util.stream.Collectors.joining(","));
        String token = jwtService.generateToken(savedUser.getUsername(), rolesStr);
        
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

        String rolesStr = user.getRoles().stream()
                .map(RoleEntity::getCode)
                .collect(java.util.stream.Collectors.joining(","));
        String token = jwtService.generateToken(user.getUsername(), rolesStr);

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

    @Override
    @Transactional
    public AuthResponse loginWithGoogle(GoogleLoginRequest request) {
        String idToken = request.getIdToken();
        String url = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;
        
        org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
        GoogleTokenInfo tokenInfo;
        try {
            tokenInfo = restTemplate.getForObject(url, GoogleTokenInfo.class);
        } catch (org.springframework.web.client.RestClientException e) {
            throw Problem.builder()
                    .withType(URI.create("https://api.app.com/errors/invalid-google-token"))
                    .withTitle("Token Google Tidak Valid")
                    .withStatus(Status.UNAUTHORIZED)
                    .withDetail("Gagal memverifikasi token Google: " + e.getMessage())
                    .build();
        }

        if (tokenInfo == null || tokenInfo.getEmail() == null) {
            throw Problem.builder()
                    .withType(URI.create("https://api.app.com/errors/invalid-google-token"))
                    .withTitle("Token Google Tidak Valid")
                    .withStatus(Status.UNAUTHORIZED)
                    .withDetail("Token Google tidak berisi informasi email pengguna.")
                    .build();
        }

        // Validasi issuer
        if (tokenInfo.getIss() == null || 
                (!tokenInfo.getIss().equals("https://accounts.google.com") && !tokenInfo.getIss().equals("accounts.google.com"))) {
            throw Problem.builder()
                    .withType(URI.create("https://api.app.com/errors/invalid-google-token"))
                    .withTitle("Token Google Tidak Valid")
                    .withStatus(Status.UNAUTHORIZED)
                    .withDetail("Issuer token Google tidak valid.")
                    .build();
        }

        // Validasi audience (client ID) jika dikonfigurasi dan bukan nilai default
        if (googleClientId != null && !googleClientId.isEmpty() && !googleClientId.equals("mock-google-client-id")) {
            if (!googleClientId.equals(tokenInfo.getAud())) {
                throw Problem.builder()
                        .withType(URI.create("https://api.app.com/errors/invalid-google-token"))
                        .withTitle("Token Google Tidak Valid")
                        .withStatus(Status.UNAUTHORIZED)
                        .withDetail("Audience token Google tidak cocok dengan Client ID aplikasi.")
                        .build();
            }
        }

        String email = tokenInfo.getEmail();
        Optional<UserEntity> userOpt = userRepository.findByEmail(email);
        UserEntity user;

        if (userOpt.isEmpty()) {
            // Register new user
            // Buat username dari email prefix
            String baseUsername = email.split("@")[0];
            String username = baseUsername;
            int suffix = 1;
            while (userRepository.existsByUsername(username)) {
                username = baseUsername + "_" + suffix;
                suffix++;
            }

            RoleEntity knightRole = roleRepository.findByCode("ROLE_KNIGHT")
                    .orElseThrow(() -> Problem.builder()
                            .withType(URI.create("https://api.app.com/errors/role-not-found"))
                            .withTitle("Role Tidak Ditemukan")
                            .withStatus(Status.INTERNAL_SERVER_ERROR)
                            .withDetail("Role default ROLE_KNIGHT tidak ditemukan di database.")
                            .build());

            user = UserEntity.builder()
                    .username(username)
                    .email(email)
                    .fullName(tokenInfo.getName() != null ? tokenInfo.getName() : baseUsername)
                    .password(passwordEncoder.encode(java.util.UUID.randomUUID().toString()))
                    .roles(java.util.Set.of(knightRole))
                    .level(1)
                    .experience(0)
                    .active(true)
                    .build();

            user = userRepository.save(user);
        } else {
            user = userOpt.get();
            if (!user.isActive()) {
                throw Problem.builder()
                        .withType(URI.create("https://api.app.com/errors/user-disabled"))
                        .withTitle("Akun Dinonaktifkan")
                        .withStatus(Status.FORBIDDEN)
                        .withDetail("Akun pengguna ini telah dinonaktifkan.")
                        .build();
            }
        }

        String rolesStr = user.getRoles().stream()
                .map(RoleEntity::getCode)
                .collect(java.util.stream.Collectors.joining(","));
        String token = jwtService.generateToken(user.getUsername(), rolesStr);

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(jwtService.getExpirationInSeconds())
                .user(mapToUserResponse(user))
                .build();
    }

    private UserResponse mapToUserResponse(UserEntity entity) {
        return UserResponse.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .fullName(entity.getFullName())
                .role(entity.getRoles().isEmpty() ? "ROLE_USER" : entity.getRoles().iterator().next().getCode())
                .roles(entity.getRoles().stream().map(RoleEntity::getCode).collect(java.util.stream.Collectors.toSet()))
                .level(entity.getLevel())
                .experience(entity.getExperience())
                .active(entity.isActive())
                .build();
    }

    @lombok.Data
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
    private static class GoogleTokenInfo {
        private String iss;
        private String sub;
        private String aud;
        private String email;
        private String name;
        private String picture;
    }
}
