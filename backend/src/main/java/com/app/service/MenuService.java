package com.app.service;

import com.app.model.MenuEntity;
import com.app.model.UserEntity;
import java.util.List;

public interface MenuService {
    List<MenuEntity> getAccessibleMenus(String username);
    UserEntity addExperience(String username, int expToAdd);
    UserEntity simulateLevelUp(String username);
}
