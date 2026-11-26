package com.makeup.entities;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

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

    @OneToOne
    @JoinColumn(name = "image_id")
    private Image image;

    @OneToMany(mappedBy = "makeup")
    private List<Image> images;

    private String imagePath;

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

    public void setId(Long id) {
        this.id = id;
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

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
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