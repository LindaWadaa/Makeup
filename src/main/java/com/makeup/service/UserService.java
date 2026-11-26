package com.makeup.service;

import com.makeup.entities.Role;
import com.makeup.entities.User;
import com.makeup.register.RegistationRequest;

public interface UserService {
    User saveUser(User user);
    User findUserByUsername(String username);
    Role addRole(Role role);
    User addRoleToUser(String username, String roleName);
    User registerUser(RegistationRequest request);
    User validateToken(String code);
    String generateCode();
    void sendEmailUser(User user, String code);
}
