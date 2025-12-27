package com.rentacar.repository;

import com.rentacar.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Araç Repository
 * Vehicle entity için veritabanı işlemleri
 */
@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    /**
     * Marka ile araç arama
     */
    List<Vehicle> findByBrand(String brand);

    /**
     * Mevcut (available) araçları listele
     */
    List<Vehicle> findByAvailableTrue();

    /**
     * Marka ve modele göre arama
     */
    List<Vehicle> findByBrandAndModel(String brand, String model);

    /**
     * Markayı içeren araçları bul (case insensitive)
     */
    List<Vehicle> findByBrandContainingIgnoreCase(String brand);
}
