package com.makeup.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.makeup.entities.Categorie;

public interface CategorieRepository extends JpaRepository<Categorie, Long> {
}