package com.makeup.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.makeup.entities.Makeup;
import com.makeup.entities.Categorie;

public interface MakeupRepository extends JpaRepository<Makeup, Long> {

    List <Makeup> findByNom(String nom);
    List <Makeup> findByNomContains(String nom);

    /*@Query("select p from Makeup p where p.categorie.nom = ?1 and p.price = ?2")
    List<Makeup> findByNomPrix (String nomCategorie, Double price);*/


    @Query("select p from Makeup p where p.nom like %:nom and p.price > :price")
    List<Makeup> findByNomPrix (@Param("nom") String nom,@Param("price") Double price);


    @Query("select p from Makeup p where p.categorie = ?1")
    List<Makeup> findByCategorie (Categorie categorie);


    List<Makeup> findByCategorie_Id(Long id);

    List<Makeup> findByOrderByNomAsc(); //asc ordre croissant


    @Query("select p from Makeup p order by p.nom ASC, p.price DESC")
    List<Makeup> trierProduitsNomsPrix ();
}
