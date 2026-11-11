package com.makeup;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.makeup.entities.Marque;
import com.makeup.entities.Makeup;
import com.makeup.repos.MarqueRepository;
import com.makeup.repos.MakeupRepository;

@SpringBootTest
class MakeupApplicationTests {

    @Autowired
    private MakeupRepository makeupRepository;

    @Autowired
    private MarqueRepository marqueRepository;

    @Test
    public void testCreateMakeup() {
        Marque marque = marqueRepository.save(
                new Marque("Anastasia Beverly Hills", "Marque de makeup de maquillage"));

        Makeup makeup6 = new Makeup("Gel sourcils", marque, "Gel teinté fixateur longue tenue", 32.00,
                LocalDate.of(2029, 3, 15));
        makeupRepository.save(makeup6);
    }

    @Test
    public void testFindMakeup() {
        Makeup p = makeupRepository.findById(1L).get();
        System.out.println(p);
    }

    @Test
    public void testUpdateMakeup() {
        Makeup p = makeupRepository.findById(1L).get();
        p.setPrice(24.99);
        makeupRepository.save(p);
        System.out.println(p);
    }

    @Test
    public void testDeleteMakeup() {
        makeupRepository.deleteById(1L);
    }

    @Test
    public void testListerTousMakeup() {
        List<Makeup> p = makeupRepository.findAll();
        for (Makeup makeup : p) {
            System.out.println(makeup);
        }
    }

    @Test
    public void testFindByNom() {
        List<Makeup> p = makeupRepository.findByNom("Palette");
        for (Makeup makeup : p) {
            System.out.println(makeup);
        }
    }

    @Test
    public void testFindByNomContains() {
        List<Makeup> p = makeupRepository.findByNomContains("P");
        for (Makeup makeup : p) {
            System.out.println(makeup);
        }
    }

    @Test
    public void testFindByNomPrix() {
        List<Makeup> p = makeupRepository.findByNomPrix("Yeux", 52.00);
        System.out.println("Nombre de resultats : " + p.size());
        for (Makeup makeup : p) {
            System.out.println(makeup);
        }
    }

    @Test
    public void testfindByMarque() {
        Marque marque = new Marque();
        marque.setIdmarque(1L);

        List<Makeup> p = makeupRepository.findByMarque(marque);
        for (Makeup makeup : p) {
            System.out.println(makeup);
        }
    }

    @Test
    public void findByMarqueIdMarque() {
        List<Makeup> makeups = makeupRepository.findByMarque_Id(1L);
        for (Makeup p : makeups) {
            System.out.println(p);
        }
    }

    @Test
    public void testFindByOrderByNomAsc() {
        List<Makeup> makeups = makeupRepository.findByOrderByNomAsc();
        for (Makeup p : makeups) {
            System.out.println(p);
        }
    }

    @Test
    public void testTrierMakeupNomsPrix() {
        List<Makeup> makeups = makeupRepository.trierMakeupNomsPrix();
        for (Makeup p : makeups) {
            System.out.println(p);
        }
    }
}
