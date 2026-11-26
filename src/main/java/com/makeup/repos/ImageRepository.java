package com.makeup.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.makeup.entities.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
