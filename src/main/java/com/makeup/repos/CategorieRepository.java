package com.makeup.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.makeup.entities.Categorie;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
@RepositoryRestResource(path = "cat")
@CrossOrigin("http://localhost:4200/") //pour autoriser angular
public interface CategorieRepository extends JpaRepository<Categorie, Long> {
}