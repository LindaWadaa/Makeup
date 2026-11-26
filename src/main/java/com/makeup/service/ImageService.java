package com.makeup.service;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.makeup.entities.Image;

public interface ImageService {
    Image uplaodImage(MultipartFile file) throws IOException;
    Image uplaodImageProd(MultipartFile file, Long idProd) throws IOException;
    List<Image> getImagesParProd(Long prodId);
    Image getImageDetails(Long id) throws IOException;
    ResponseEntity<byte[]> getImage(Long id) throws IOException;
    void deleteImage(Long id);
    void uploadImageFS(MultipartFile file, Long id) throws IOException;
    byte[] getImageFS(Long id) throws IOException;
}
