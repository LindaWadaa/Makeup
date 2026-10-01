package com.makeup.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.makeup.entities.Categorie;
import com.makeup.entities.Makeup;
import com.makeup.repos.MakeupRepository;

@Service
public class MakeupServiceImpl implements MakeupService {

    private final MakeupRepository makeupRepository;

    public MakeupServiceImpl(MakeupRepository makeupRepository) {
        this.makeupRepository = makeupRepository;
    }

    @Override
    public Makeup saveMakeup(Makeup makeup) {
        return makeupRepository.save(makeup);
    }

    @Override
    public Makeup updateMakeup(Makeup makeup) {
        return makeupRepository.save(makeup);
    }

    @Override
    public void deleteMakeup(Makeup makeup) {
        makeupRepository.delete(makeup);
    }

    @Override
    public void deleteMakeupById(Long id) {
        makeupRepository.deleteById(id);
    }

    @Override
    public Makeup getMakeup(Long id) {
        return makeupRepository.findById(id).orElseThrow();
    }

    @Override
    public List<Makeup> getAllMakeups() {
        return makeupRepository.findAll();
    }

    @Override
    public List<Makeup> findByNomMakeup(String nom) {
        return makeupRepository.findByNom(nom);
    }

    @Override
    public List<Makeup> findByNomMakeupContains(String nom) {
        return makeupRepository.findByNomContains(nom);
    }

    @Override
    public List<Makeup> findByNomPrix(String nom, Double prix) {
        return makeupRepository.findByNomPrix(nom, prix);
    }

    @Override
    public List<Makeup> findByCategorie(Categorie categorie) {
        return makeupRepository.findByCategorie(categorie);
    }

    @Override
    public List<Makeup> findByCategorieIdCat(Long id) {
        return makeupRepository.findByCategorie_Id(id);
    }

    @Override
    public List<Makeup> findByOrderByNomMakeupAsc() {
        return makeupRepository.findByOrderByNomAsc();
    }

    @Override
    public List<Makeup> trierMakeupNomsPrix() {
        return makeupRepository.trierProduitsNomsPrix();
    }
}
