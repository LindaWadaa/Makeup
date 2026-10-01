package com.makeup;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.makeup.entities.Categorie;
import com.makeup.entities.Makeup;
import com.makeup.repos.CategorieRepository;
import com.makeup.repos.MakeupRepository;


@SpringBootTest
class MakeupApplicationTests {

    @Autowired
    private MakeupRepository makeupRepository;

    @Autowired
    private CategorieRepository categorieRepository;
    
    @Test   
    public void testCreateMakeup() {
        Categorie categorie = categorieRepository.save(
            new Categorie("Yeux", "Produits pour le maquillage des yeux"));
        Makeup prod3 = new Makeup("Palette", categorie, "Urban Decay", "Palette de 12 fards nudes", 52.00, LocalDate.of(2028, 9, 10));
        makeupRepository.save(prod3);  //save dans la base de données
    }



    @Test
    public void testFindMakeup() {
        Makeup p = makeupRepository.findById(1L).get(); //L : type long
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
        for (Makeup prod : p) {
            System.out.println(prod);
        }
    }


    @Test
    public void testFindByNom() {
        List<Makeup> p = makeupRepository.findByNom("Palette");
        for (Makeup prod : p) {
            System.out.println(prod);
        }
    }

    @Test
    public void testFindByNomContains() {
        List<Makeup> p = makeupRepository.findByNomContains("P");
        for (Makeup prod : p) {
            System.out.println(prod);
        }
    }


    @Test
    public void testFindByNomPrix() {
        List<Makeup> p = makeupRepository.findByNomPrix("Yeux", 52.00);
        System.out.println("Nombre de resultats : " + p.size());
        for (Makeup prod : p) {
            System.out.println(prod);
        }
    }



    @Test
    public void testfindByCategorie(){
        Categorie cat = new Categorie();
        cat.setIdcat(1L);

        List<Makeup> p = makeupRepository.findByCategorie(cat);
        for (Makeup prod : p) {
            System.out.println(prod);
        }
    }


    @Test
    public void findByCategorieIdCat(){
        List<Makeup> prods = makeupRepository.findByCategorie_Id(1L);
        for (Makeup p : prods){            System.out.println(p);
        }
    }



    @Test
    public void testFindByOrderByNomAsc(){

    List<Makeup> prods =
    makeupRepository.findByOrderByNomAsc();
    for (Makeup p : prods){

    System.out.println(p);}
    }




    @Test
    public void testTrierProduitsNomsPrix(){

    List<Makeup> prods = makeupRepository.trierProduitsNomsPrix();
        for (Makeup p : prods)
        System.out.println(p);



}








}





    
