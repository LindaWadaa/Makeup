package com.makeup.service;
import java.util.List;

import com.makeup.dto.MakeupDTO;
import com.makeup.entities.Makeup;
import com.makeup.entities.Marque;

public interface MakeupService {
    MakeupDTO saveMakeup(MakeupDTO p);
    MakeupDTO updateMakeup(MakeupDTO p);
    void deleteMakeup(Makeup p);
    void deleteMakeupById(Long id);
    MakeupDTO getMakeup(Long id);
    List<MakeupDTO> getAllMakeups();
    MakeupDTO convertEntityToDto(Makeup makeup);
    Makeup convertDtoToEntity(MakeupDTO makeupDto);


    List<Makeup> findByNomMakeup(String nom);
    List<Makeup> findByNomMakeupContains(String nom);
    List<Makeup> findByNomPrix (String nom, Double prix);
    List<Makeup> findByMarque (Marque marque);
    List<Makeup> findByMarqueIdMarque(Long id);
    List<Makeup> findByOrderByNomMakeupAsc();
    List<Makeup> trierMakeupNomsPrix();
}