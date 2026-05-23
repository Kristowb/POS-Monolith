package com.app.service;

import com.app.dto.UserRequest;
import com.app.dto.UserResponse;
import com.app.model.UserEntity;
import com.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> Problem.builder()
                        .withType(URI.create("https://api.app.com/errors/user-not-found"))
                        .withTitle("User Tidak Ditemukan")
                        .withStatus(Status.NOT_FOUND)
                        .withDetail("User dengan ID " + id + " tidak ditemukan dalam sistem kami.")
                        .build());
    }

    @Override
    @Transactional
    public UserResponse createUser(UserRequest request) {
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
                .role(request.getRole())
                .active(request.getActive() != null ? request.getActive() : true)
                .build();

        UserEntity savedUser = userRepository.save(user);
        return mapToResponse(savedUser);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UserRequest request) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> Problem.builder()
                        .withType(URI.create("https://api.app.com/errors/user-not-found"))
                        .withTitle("User Tidak Ditemukan")
                        .withStatus(Status.NOT_FOUND)
                        .withDetail("User dengan ID " + id + " tidak ditemukan dalam sistem kami.")
                        .build());

        // Validasi username unik jika berubah
        if (!user.getUsername().equals(request.getUsername()) && userRepository.existsByUsername(request.getUsername())) {
            throw Problem.builder()
                    .withType(URI.create("https://api.app.com/errors/duplicate-username"))
                    .withTitle("Username Duplikat")
                    .withStatus(Status.CONFLICT)
                    .withDetail("Username '" + request.getUsername() + "' sudah digunakan oleh pengguna lain.")
                    .build();
        }
        // Validasi email unik jika berubah
        if (!user.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw Problem.builder()
                    .withType(URI.create("https://api.app.com/errors/duplicate-email"))
                    .withTitle("Email Duplikat")
                    .withStatus(Status.CONFLICT)
                    .withDetail("Email '" + request.getEmail() + "' sudah digunakan oleh pengguna lain.")
                    .build();
        }

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setRole(request.getRole());
        if (request.getActive() != null) {
            user.setActive(request.getActive());
        }

        UserEntity updatedUser = userRepository.save(user);
        return mapToResponse(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw Problem.builder()
                    .withType(URI.create("https://api.app.com/errors/user-not-found"))
                    .withTitle("User Tidak Ditemukan")
                    .withStatus(Status.NOT_FOUND)
                    .withDetail("User dengan ID " + id + " tidak ditemukan dalam sistem kami.")
                    .build();
        }
        userRepository.deleteById(id);
    }

    private UserResponse mapToResponse(UserEntity entity) {
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
