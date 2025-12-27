package com.rentacar.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Araç entity sınıfı
 * Test sürüşü için mevcut araçları temsil eder
 */
@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Marka boş olamaz")
    @Column(nullable = false)
    private String brand;

    @NotBlank(message = "Model boş olamaz")
    @Column(nullable = false)
    private String model;

    @NotNull(message = "Yıl boş olamaz")
    @Min(value = 2000, message = "Yıl en az 2000 olmalı")
    @Column(name = "production_year", nullable = false)
    private Integer year;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private boolean available = true;

    // Constructors
    public Vehicle() {
    }

    public Vehicle(String brand, String model, Integer year) {
        this.brand = brand;
        this.model = model;
        this.year = year;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    // Utility method
    public String getFullName() {
        return brand + " " + model + " (" + year + ")";
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "id=" + id +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", year=" + year +
                '}';
    }
}
