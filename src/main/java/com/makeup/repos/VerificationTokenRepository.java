package com.makeup.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.makeup.entities.VerificationToken;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    VerificationToken findByToken(String token);
}
