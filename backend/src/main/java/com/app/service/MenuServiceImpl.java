package com.app.service;

import com.app.model.MenuEntity;
import com.app.model.RoleEntity;
import com.app.model.UserEntity;
import com.app.repository.MenuRepository;
import com.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final UserRepository userRepository;
    private final MenuRepository menuRepository;

    @Override
    @Transactional(readOnly = true)
    public List<MenuEntity> getAccessibleMenus(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> Problem.builder()
                        .withType(URI.create("https://api.app.com/errors/user-not-found"))
                        .withTitle("User Tidak Ditemukan")
                        .withStatus(Status.NOT_FOUND)
                        .withDetail("Pengguna '" + username + "' tidak ditemukan.")
                        .build());

        boolean isAdmin = user.getRoles().stream()
                .anyMatch(r -> r.getCode().equalsIgnoreCase("ROLE_ADMIN"));

        List<MenuEntity> allMenus = menuRepository.findAll();

        if (isAdmin) {
            return allMenus;
        }

        Set<Long> userRoleIds = user.getRoles().stream()
                .map(RoleEntity::getId)
                .collect(Collectors.toSet());

        return allMenus.stream()
                .filter(menu -> menu.getMinLevel() <= user.getLevel())
                .filter(menu -> menu.getRoles().stream()
                        .anyMatch(role -> userRoleIds.contains(role.getId())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserEntity addExperience(String username, int expToAdd) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> Problem.builder()
                        .withType(URI.create("https://api.app.com/errors/user-not-found"))
                        .withTitle("User Tidak Ditemukan")
                        .withStatus(Status.NOT_FOUND)
                        .withDetail("Pengguna '" + username + "' tidak ditemukan.")
                        .build());

        int currentExp = user.getExperience() + expToAdd;
        user.setExperience(currentExp);

        int currentLevel = user.getLevel();
        int expNeeded = currentLevel * 100;

        while (user.getExperience() >= expNeeded) {
            user.setExperience(user.getExperience() - expNeeded);
            user.setLevel(user.getLevel() + 1);
            expNeeded = user.getLevel() * 100;
        }

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public UserEntity simulateLevelUp(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> Problem.builder()
                        .withType(URI.create("https://api.app.com/errors/user-not-found"))
                        .withTitle("User Tidak Ditemukan")
                        .withStatus(Status.NOT_FOUND)
                        .withDetail("Pengguna '" + username + "' tidak ditemukan.")
                        .build());

        user.setLevel(user.getLevel() + 1);
        user.setExperience(0);
        return userRepository.save(user);
    }
}
