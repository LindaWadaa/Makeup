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
    private double price;
    private String description;
    private LocalDate dateExpiration;

    @ManyToOne
    @JoinColumn(name = "marque_id")
    private Marque marque;

    public Makeup() {
    }

    public Makeup(String nom, Marque marque, String description, double price
                  , LocalDate dateExpiration) {
        this.nom = nom;
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

    public Marque getMarque() {
        return marque;
    }

    public void setMarque(Marque marque) {
        this.marque = marque;
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
                ", marque=" + marque +
                ", price=" + price +
                ", description='" + description + '\'' +
                ", dateExpiration=" + dateExpiration +
                '}';
    }
}