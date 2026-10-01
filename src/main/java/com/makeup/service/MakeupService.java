package com.makeup.service;
import java.util.List;

import com.makeup.entities.Makeup;
import com.makeup.entities.Categorie;

public interface MakeupService {
    Makeup saveMakeup(Makeup p);
    Makeup updateMakeup(Makeup p);
    void deleteMakeup(Makeup p);
    void deleteMakeupById(Long id);
    Makeup getMakeup(Long id);
    List<Makeup> getAllMakeups();


    List<Makeup> findByNomMakeup(String nom);
    List<Makeup> findByNomMakeupContains(String nom);
    List<Makeup> findByNomPrix (String nom, Double prix);
    List<Makeup> findByCategorie (Categorie categorie);
    List<Makeup> findByCategorieIdCat(Long id);
    List<Makeup> findByOrderByNomMakeupAsc();
    List<Makeup> trierMakeupNomsPrix();
}