package com.makeup.repos;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.makeup.entities.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRole(String role);
}
