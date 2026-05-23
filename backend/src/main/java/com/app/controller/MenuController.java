package com.app.controller;

import com.app.dto.UserResponse;
import com.app.model.MenuEntity;
import com.app.model.RoleEntity;
import com.app.model.UserEntity;
import com.app.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @GetMapping
    public ResponseEntity<List<MenuEntity>> getAccessibleMenus(Principal principal) {
        if (principal == null) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(menuService.getAccessibleMenus(principal.getName()));
    }

    @PostMapping("/simulate-exp")
    public ResponseEntity<UserResponse> simulateExp(Principal principal, @RequestParam(defaultValue = "15") int exp) {
        if (principal == null) {
            return ResponseEntity.badRequest().build();
        }
        UserEntity updatedUser = menuService.addExperience(principal.getName(), exp);
        return ResponseEntity.ok(mapToUserResponse(updatedUser));
    }

    @PostMapping("/simulate-levelup")
    public ResponseEntity<UserResponse> simulateLevelUp(Principal principal) {
        if (principal == null) {
            return ResponseEntity.badRequest().build();
        }
        UserEntity updatedUser = menuService.simulateLevelUp(principal.getName());
        return ResponseEntity.ok(mapToUserResponse(updatedUser));
    }

    private UserResponse mapToUserResponse(UserEntity entity) {
        return UserResponse.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .fullName(entity.getFullName())
                .role(entity.getRoles().isEmpty() ? "ROLE_USER" : entity.getRoles().iterator().next().getCode())
                .roles(entity.getRoles().stream().map(RoleEntity::getCode).collect(Collectors.toSet()))
                .level(entity.getLevel())
                .experience(entity.getExperience())
                .active(entity.isActive())
                .build();
    }
}
