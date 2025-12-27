package com.rentacar.integration;

import com.rentacar.model.Vehicle;
import com.rentacar.repository.VehicleRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * VehicleRepository Integration Testleri
 * 
 * Test Seviyesi: INTEGRATION TEST
 * Real database (H2) ile repository testleri
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("VehicleRepository Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class VehicleRepositoryIntegrationTest {

    @Autowired
    private VehicleRepository vehicleRepository;

    @BeforeEach
    void setUp() {
        vehicleRepository.deleteAll();
    }

    @Test
    @Order(1)
    @DisplayName("VIT-01: Araç kaydedilip getirilebilmeli")
    void save_ShouldPersistVehicle() {
        // Given
        Vehicle vehicle = new Vehicle("BMW", "320i", 2024);
        vehicle.setDescription("Sportif sedan");

        // When
        Vehicle saved = vehicleRepository.save(vehicle);

        // Then
        assertNotNull(saved.getId());
        assertEquals("BMW", saved.getBrand());
        assertEquals("320i", saved.getModel());
    }

    @Test
    @Order(2)
    @DisplayName("VIT-02: ID ile araç getirilebilmeli")
    void findById_WhenExists_ShouldReturnVehicle() {
        // Given
        Vehicle vehicle = vehicleRepository.save(new Vehicle("Mercedes", "C200", 2023));

        // When
        Optional<Vehicle> result = vehicleRepository.findById(vehicle.getId());

        // Then
        assertTrue(result.isPresent());
        assertEquals("Mercedes", result.get().getBrand());
    }

    @Test
    @Order(3)
    @DisplayName("VIT-03: Olmayan ID için empty dönmeli")
    void findById_WhenNotExists_ShouldReturnEmpty() {
        // When
        Optional<Vehicle> result = vehicleRepository.findById(999L);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    @Order(4)
    @DisplayName("VIT-04: Marka ile arama yapılabilmeli")
    void findByBrand_ShouldReturnMatchingVehicles() {
        // Given
        vehicleRepository.save(new Vehicle("Toyota", "Corolla", 2023));
        vehicleRepository.save(new Vehicle("Toyota", "Camry", 2024));
        vehicleRepository.save(new Vehicle("BMW", "320i", 2023));

        // When
        List<Vehicle> result = vehicleRepository.findByBrand("Toyota");

        // Then
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(v -> v.getBrand().equals("Toyota")));
    }

    @Test
    @Order(5)
    @DisplayName("VIT-05: Mevcut (available) araçlar listelenebilmeli")
    void findByAvailableTrue_ShouldReturnOnlyAvailable() {
        // Given
        Vehicle available1 = new Vehicle("Audi", "A3", 2024);
        available1.setAvailable(true);
        vehicleRepository.save(available1);

        Vehicle available2 = new Vehicle("VW", "Golf", 2023);
        available2.setAvailable(true);
        vehicleRepository.save(available2);

        Vehicle notAvailable = new Vehicle("Ford", "Focus", 2022);
        notAvailable.setAvailable(false);
        vehicleRepository.save(notAvailable);

        // When
        List<Vehicle> result = vehicleRepository.findByAvailableTrue();

        // Then
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(Vehicle::isAvailable));
    }

    @Test
    @Order(6)
    @DisplayName("VIT-06: Marka ve model ile arama yapılabilmeli")
    void findByBrandAndModel_ShouldReturnExactMatch() {
        // Given
        vehicleRepository.save(new Vehicle("Honda", "Civic", 2023));
        vehicleRepository.save(new Vehicle("Honda", "Accord", 2024));

        // When
        List<Vehicle> result = vehicleRepository.findByBrandAndModel("Honda", "Civic");

        // Then
        assertEquals(1, result.size());
        assertEquals("Civic", result.get(0).getModel());
    }

    @Test
    @Order(7)
    @DisplayName("VIT-07: Case-insensitive marka araması")
    void findByBrandContainingIgnoreCase_ShouldIgnoreCase() {
        // Given
        vehicleRepository.save(new Vehicle("TOYOTA", "Corolla", 2023));
        vehicleRepository.save(new Vehicle("Toyota", "Camry", 2024));

        // When
        List<Vehicle> result = vehicleRepository.findByBrandContainingIgnoreCase("toyota");

        // Then
        assertEquals(2, result.size());
    }

    @Test
    @Order(8)
    @DisplayName("VIT-08: Araç güncellenebilmeli")
    void update_ShouldPersistChanges() {
        // Given
        Vehicle vehicle = vehicleRepository.save(new Vehicle("Opel", "Astra", 2022));
        Long id = vehicle.getId();

        // When
        vehicle.setYear(2023);
        vehicle.setDescription("Güncellendi");
        vehicleRepository.save(vehicle);

        // Then
        Vehicle updated = vehicleRepository.findById(id).orElseThrow();
        assertEquals(2023, updated.getYear());
        assertEquals("Güncellendi", updated.getDescription());
    }

    @Test
    @Order(9)
    @DisplayName("VIT-09: Araç silinebilmeli")
    void delete_ShouldRemoveVehicle() {
        // Given
        Vehicle vehicle = vehicleRepository.save(new Vehicle("Fiat", "Egea", 2023));
        Long id = vehicle.getId();

        // When
        vehicleRepository.deleteById(id);

        // Then
        assertFalse(vehicleRepository.findById(id).isPresent());
    }

    @Test
    @Order(10)
    @DisplayName("VIT-10: Tüm araç sayısı doğru olmalı")
    void count_ShouldReturnCorrectNumber() {
        // Given
        vehicleRepository.save(new Vehicle("Car1", "Model1", 2020));
        vehicleRepository.save(new Vehicle("Car2", "Model2", 2021));
        vehicleRepository.save(new Vehicle("Car3", "Model3", 2022));

        // When
        long count = vehicleRepository.count();

        // Then
        assertEquals(3, count);
    }
}
