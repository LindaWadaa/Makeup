package com.makeup.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.makeup.entities.Image;
import com.makeup.entities.Makeup;
import com.makeup.repos.ImageRepository;
import com.makeup.repos.MakeupRepository;

@Service
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final MakeupRepository makeupRepository;

    public ImageServiceImpl(ImageRepository imageRepository, MakeupRepository makeupRepository) {
        this.imageRepository = imageRepository;
        this.makeupRepository = makeupRepository;
    }

    @Override
    public Image uplaodImage(MultipartFile file) throws IOException {
        return imageRepository.save(Image.builder()
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .image(file.getBytes())
                .build());
    }

    @Override
    public Image uplaodImageProd(MultipartFile file, Long idProd) throws IOException {
        Makeup m = new Makeup();
        m.setId(idProd);
        return imageRepository.save(Image.builder()
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .image(file.getBytes())
                .makeup(m)
                .build());
    }

    @Override
    public List<Image> getImagesParProd(Long prodId) {
        Makeup m = makeupRepository.findById(prodId).get();
        return m.getImages();
    }

    @Override
    public Image getImageDetails(Long id) throws IOException {
        final Optional<Image> dbImage = imageRepository.findById(id);
        return Image.builder()
                .idImage(dbImage.get().getIdImage())
                .name(dbImage.get().getName())
                .type(dbImage.get().getType())
                .image(dbImage.get().getImage())
                .build();
    }

    @Override
    public ResponseEntity<byte[]> getImage(Long id) throws IOException {
        final Optional<Image> dbImage = imageRepository.findById(id);
        return ResponseEntity
                .ok()
                .contentType(MediaType.valueOf(dbImage.get().getType()))
                .body(dbImage.get().getImage());
    }

    @Override
    public void deleteImage(Long id) {
        imageRepository.deleteById(id);
    }

    @Override
    public void uploadImageFS(MultipartFile file, Long id) throws IOException {
        Makeup m = makeupRepository.findById(id).orElseThrow();
        m.setImagePath(id + ".jpg");
        Files.createDirectories(Paths.get(System.getProperty("user.home") + "/images/"));
        Files.write(Paths.get(System.getProperty("user.home") + "/images/" + m.getImagePath()), file.getBytes());
        makeupRepository.save(m);
    }

    @Override
    public byte[] getImageFS(Long id) throws IOException {
        Makeup m = makeupRepository.findById(id).orElseThrow();
        return Files.readAllBytes(Paths.get(System.getProperty("user.home") + "/images/" + m.getImagePath()));
    }
}
