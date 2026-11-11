package com.makeup.RestControllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.makeup.entities.Marque;
import com.makeup.repos.MarqueRepository;

@RestController
@RequestMapping("/api/marque")
@CrossOrigin("http://localhost:4200")
public class MarqueRESTController {

    private final MarqueRepository marqueRepository;

    public MarqueRESTController(MarqueRepository marqueRepository) {
        this.marqueRepository = marqueRepository;
    }

    @GetMapping("/all")
    public List<Marque> getAllMarques() {
        return marqueRepository.findAll();
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Marque> getMarqueById(@PathVariable Long id) {
        return marqueRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/add")
    public Marque ajouterMarque(@RequestBody Marque marque) {
        marque.setId(null);
        return marqueRepository.save(marque);
    }

    @PutMapping("/update")
    public ResponseEntity<Marque> modifierMarque(@RequestBody Marque marque) {
        if (marque.getId() == null || !marqueRepository.existsById(marque.getId())) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(marqueRepository.save(marque));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> supprimerMarque(@PathVariable Long id) {
        if (!marqueRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        marqueRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
