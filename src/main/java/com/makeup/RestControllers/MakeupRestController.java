package com.makeup.RestControllers;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.makeup.entities.Makeup;
import com.makeup.service.MakeupService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@AllArgsConstructor
@RestController
@CrossOrigin
public class MakeupRestController {

    private final MakeupService makeupService;

    public MakeupRestController(MakeupService makeupService) {
        this.makeupService = makeupService;
    }

    @GetMapping("/")
    public String health() {
        return "Makeup API is running";
    }

    @GetMapping("/api")
    public List<Makeup> getAllMakeups() {
        return makeupService.getAllMakeups();
    }

    @GetMapping("/api/{id}")
    public Makeup getProduitById(@PathVariable("id") Long id) {
        return makeupService.getMakeup(id);
    }

    @RequestMapping(method = RequestMethod. POST)
    public Makeup createProduit(@RequestBody Makeup makeup) {
        return makeupService.saveMakeup(makeup);
    }   

    
}
