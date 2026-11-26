package com.makeup.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;
import org.springframework.security.access.prepost.PreAuthorize;

import com.makeup.dto.MakeupDTO;
import com.makeup.entities.Marque;
import com.makeup.entities.Makeup;
import com.makeup.repos.ImageRepository;
import com.makeup.repos.MakeupRepository;

@Service
public class MakeupServiceImpl implements MakeupService {

    private final MakeupRepository makeupRepository;
    private final ImageRepository imageRepository;
    private final ModelMapper modelMapper;

    public MakeupServiceImpl(MakeupRepository makeupRepository, ImageRepository imageRepository, ModelMapper modelMapper) {
        this.makeupRepository = makeupRepository;
        this.imageRepository = imageRepository;
        this.modelMapper = modelMapper;
    }





    @PreAuthorize("hasAuthority('ADMIN')")
    @Override
    public MakeupDTO saveMakeup(MakeupDTO p) {
        return convertEntityToDto(makeupRepository.save(convertDtoToEntity(p)));
    }

    @Override
    public MakeupDTO updateMakeup(MakeupDTO p) {
        Makeup makeup = convertDtoToEntity(p);
        Makeup existing = makeupRepository.findById(makeup.getId()).orElseThrow();
        Long oldProdImageId = existing.getImage() != null ? existing.getImage().getIdImage() : null;
        Long newProdImageId = makeup.getImage() != null ? makeup.getImage().getIdImage() : null;
        Makeup prodUpdated = makeupRepository.save(makeup);
        if (oldProdImageId != null && !oldProdImageId.equals(newProdImageId)) {
            imageRepository.deleteById(oldProdImageId);
        }
        return convertEntityToDto(prodUpdated);
    }

    @Override
    public void deleteMakeup(Makeup makeup) {
        makeupRepository.delete(makeup);
    }

    @Override
    public void deleteMakeupById(Long id) {
        Makeup m = makeupRepository.findById(id).orElse(null);
        if (m != null && m.getImagePath() != null) {
            try {
                Files.deleteIfExists(Paths.get(System.getProperty("user.home") + "/images/" + m.getImagePath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        makeupRepository.deleteById(id);
    }

    @Override
    public MakeupDTO getMakeup(Long id) {
        return convertEntityToDto(makeupRepository.findById(id).orElseThrow());
    }

    @Override
    public List<MakeupDTO> getAllMakeups() {
        return makeupRepository.findAll().stream()
                .map(this::convertEntityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public MakeupDTO convertEntityToDto(Makeup makeup) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
        MakeupDTO makeupDTO = modelMapper.map(makeup, MakeupDTO.class);
        return makeupDTO;
    }

    @Override
    public Makeup convertDtoToEntity(MakeupDTO makeupDto) {
        Makeup makeup = new Makeup();
        makeup = modelMapper.map(makeupDto, Makeup.class);
        return makeup;
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
    public List<Makeup> findByMarque(Marque marque) {
        return makeupRepository.findByMarque(marque);
    }

    @Override
    public List<Makeup> findByMarqueIdMarque(Long id) {
        return makeupRepository.findByMarque_Id(id);
    }

    @Override
    public List<Makeup> findByOrderByNomMakeupAsc() {
        return makeupRepository.findByOrderByNomAsc();
    }

    @Override
    public List<Makeup> trierMakeupNomsPrix() {
        return makeupRepository.trierMakeupNomsPrix();
    }
}
