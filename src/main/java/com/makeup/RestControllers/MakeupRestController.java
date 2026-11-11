package com.makeup.RestControllers;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.security.access.prepost.PreAuthorize;

import com.makeup.entities.Makeup;
import com.makeup.service.MakeupService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
@CrossOrigin
public class MakeupRestController {

    private final MakeupService makeupService;

    public MakeupRestController(MakeupService makeupService) {
        this.makeupService = makeupService;
    }

    @GetMapping("/health")
    public String health() {
        return "Makeup API is running";
    }

    @GetMapping("/allmakeups")
    public List<Makeup> getAllMakeups() {
        return makeupService.getAllMakeups();
    }

    @GetMapping("/getmakeupbyname/{nom}")
    public List<Makeup> findByNomMakeupContains(@PathVariable("nom") String nom) {
        return makeupService.findByNomMakeupContains(nom);
    }

    @GetMapping("/getmakeup/{id}")
    public Makeup getMakeupById(@PathVariable("id") Long id) {
        return makeupService.getMakeup(id);
    }





    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/addmakeup")
    public Makeup createMakeup(@RequestBody Makeup makeup) {
        return makeupService.saveMakeup(makeup);
    }



    

    @PutMapping("/updatemakeup")
    public Makeup updateMakeup(@RequestBody Makeup makeup) {
        return makeupService.updateMakeup(makeup);
    }

    @DeleteMapping("/deletemakeup/{id}")
    public void deleteMakeup(@PathVariable("id") Long id) {
        makeupService.deleteMakeupById(id);
    }

    @GetMapping("/getmakeupbymarque/{idMarque}")
    public List<Makeup> getMakeupsByMarqueId(@PathVariable("idMarque") Long idMarque) {
        return makeupService.findByMarqueIdMarque(idMarque);
    }
}
