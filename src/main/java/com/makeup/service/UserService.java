package com.makeup.service;

import com.makeup.entities.Role;
import com.makeup.entities.User;

public interface UserService {
    User saveUser(User user);
    User findUserByUsername(String username);
    Role addRole(Role role);
    User addRoleToUser(String username, String roleName);
}
