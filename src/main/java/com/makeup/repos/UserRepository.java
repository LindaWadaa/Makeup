package com.makeup.repos;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.makeup.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select distinct u from User u left join fetch u.roles where u.username = :username")
    Optional<User> findByUsername(@Param("username") String username);
    boolean existsByUsername(String username);
}
