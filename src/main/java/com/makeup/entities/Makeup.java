package com.makeup.entities;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Makeup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//autoincrement
    private Long id;

    private String nom;
    private String marque;
    private double price;
    private String description;
    private LocalDate dateExpiration;

    @ManyToOne
    @JoinColumn(name = "categorie_id")
    private Categorie categorie;

    public Makeup() {
    }

    public Makeup(String nom, Categorie categorie, String marque, String description, double price
                  , LocalDate dateExpiration) {
        this.nom = nom;
        this.categorie = categorie;
        this.marque = marque;
        this.description = description;
        this.price = price;
        this.dateExpiration = dateExpiration;
    }

    public Long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Categorie getCategorie() {
        return categorie;
    }

    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getMarque() {
        return marque;
    }   

    public void setMarque(String marque) {
        this.marque = marque;
    }   

    public LocalDate getDateExpiration() {
        return dateExpiration;
    }

    public void setDateExpiration(LocalDate dateExpiration) {
        this.dateExpiration = dateExpiration;
    }

    @Override
    public String toString() {
        return "Makeup{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", marque='" + marque + '\'' +
                ", price=" + price +
                ", description='" + description + '\'' +
                ", dateExpiration=" + dateExpiration +
                '}';
    }
}