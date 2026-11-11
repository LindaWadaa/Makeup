package com.makeup.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.makeup.entities.Role;
import com.makeup.entities.User;
import com.makeup.repos.RoleRepository;
import com.makeup.repos.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User addRoleToUser(String username, String roleName) {
        User user = findUserByUsername(username);
        Role role = roleRepository.findByRole(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Role introuvable : " + roleName));
        if (user.getRoles().stream().noneMatch(existing -> existing.getRole().equals(roleName))) {
            user.getRoles().add(role);
        }
        return user;
    }

    @Override
    public Role addRole(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
}
