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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.security.access.prepost.PreAuthorize;

import com.makeup.dto.MakeupDTO;
import com.makeup.entities.Makeup;
import com.makeup.service.MakeupService;

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

    @RequestMapping(path = "/allmakeups", method = RequestMethod.GET)
    public List<MakeupDTO> getAllMakeups() {
        return makeupService.getAllMakeups();
    }

    @GetMapping("/getmakeupbyname/{nom}")
    public List<Makeup> findByNomMakeupContains(@PathVariable("nom") String nom) {
        return makeupService.findByNomMakeupContains(nom);
    }

    @RequestMapping(value = "/getmakeup/{id}", method = RequestMethod.GET)
    public MakeupDTO getMakeupById(@PathVariable("id") Long id) {
        return makeupService.getMakeup(id);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(path = "/addmakeup", method = RequestMethod.POST)
    public MakeupDTO createMakeup(@RequestBody MakeupDTO makeupDTO) {
        return makeupService.saveMakeup(makeupDTO);
    }

    @RequestMapping(path = "/updatemakeup", method = RequestMethod.PUT)
    public MakeupDTO updateMakeup(@RequestBody MakeupDTO makeupDTO) {
        return makeupService.updateMakeup(makeupDTO);
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
