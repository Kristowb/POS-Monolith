package com.app.config;

import com.app.model.MenuEntity;
import com.app.model.RoleEntity;
import com.app.model.UserEntity;
import com.app.repository.MenuRepository;
import com.app.repository.RoleRepository;
import com.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final MenuRepository menuRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 1. Seed Roles
        RoleEntity knight = getOrCreateRole("Knight", "ROLE_KNIGHT", "Regular POS User - focused on basic cashier operations");
        RoleEntity archer = getOrCreateRole("Archer", "ROLE_ARCHER", "Pro POS Sales User - has access to sales analytics at Level 5");
        RoleEntity warrior = getOrCreateRole("Warrior", "ROLE_WARRIOR", "Pro POS Inventory User - has access to warehouse stock at Level 3");
        RoleEntity admin = getOrCreateRole("Admin", "ROLE_ADMIN", "System Administrator with full access");

        // 2. Seed Menus
        getOrCreateMenu("MENU_CASHIER", "Kasir", "/cashier", "shopping-cart", 1, Set.of(knight, archer, warrior));
        getOrCreateMenu("MENU_INVENTORY", "Gudang & Inventaris", "/inventory", "archive", 3, Set.of(warrior));
        getOrCreateMenu("MENU_ANALYTICS", "Analitik Penjualan", "/analytics", "bar-chart-2", 5, Set.of(archer));
        getOrCreateMenu("MENU_SETTINGS", "Pengaturan Sistem", "/settings", "settings", 10, new HashSet<>());

        // 3. Seed Default Users
        if (!userRepository.existsByUsername("admin")) {
            UserEntity adminUser = UserEntity.builder()
                    .username("admin")
                    .email("admin@novapos.com")
                    .fullName("NovaPOS Administrator")
                    .password(passwordEncoder.encode("admin"))
                    .roles(Set.of(admin))
                    .level(10)
                    .experience(0)
                    .active(true)
                    .build();
            userRepository.save(adminUser);
        }

        if (!userRepository.existsByUsername("knight")) {
            UserEntity knightUser = UserEntity.builder()
                    .username("knight")
                    .email("knight@novapos.com")
                    .fullName("Sir Galahad the Knight")
                    .password(passwordEncoder.encode("password"))
                    .roles(Set.of(knight))
                    .level(1)
                    .experience(0)
                    .active(true)
                    .build();
            userRepository.save(knightUser);
        }
    }

    private RoleEntity getOrCreateRole(String name, String code, String description) {
        return roleRepository.findByCode(code).orElseGet(() -> {
            RoleEntity role = RoleEntity.builder()
                    .name(name)
                    .code(code)
                    .description(description)
                    .build();
            return roleRepository.save(role);
        });
    }

    private void getOrCreateMenu(String code, String title, String path, String icon, int minLevel, Set<RoleEntity> roles) {
        menuRepository.findByCode(code).orElseGet(() -> {
            MenuEntity menu = MenuEntity.builder()
                    .code(code)
                    .title(title)
                    .path(path)
                    .icon(icon)
                    .minLevel(minLevel)
                    .roles(new HashSet<>(roles))
                    .build();
            return menuRepository.save(menu);
        });
    }
}
