package com.app.repository;

import com.app.model.MenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<MenuEntity, Long> {
    Optional<MenuEntity> findByCode(String code);
}
