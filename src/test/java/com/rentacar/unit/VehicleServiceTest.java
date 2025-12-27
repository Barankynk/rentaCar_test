package com.rentacar.unit;

import com.rentacar.exception.ResourceNotFoundException;
import com.rentacar.model.Vehicle;
import com.rentacar.repository.VehicleRepository;
import com.rentacar.service.VehicleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * VehicleService Unit Testleri
 * 
 * Test Seviyesi: UNIT TEST
 * Araç: Mockito
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("VehicleService Unit Tests")
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private VehicleService vehicleService;

    private Vehicle testVehicle;

    @BeforeEach
    void setUp() {
        testVehicle = new Vehicle("Toyota", "Corolla", 2023);
        testVehicle.setId(1L);
        testVehicle.setDescription("Test araç");
        testVehicle.setAvailable(true);
    }

    @Nested
    @DisplayName("Araç Listeleme Testleri")
    class ListTests {

        @Test
        @DisplayName("VS-01: Tüm araçlar listelenebilmeli")
        void getAllVehicles_ShouldReturnAllVehicles() {
            // Given
            Vehicle v1 = new Vehicle("Toyota", "Corolla", 2023);
            Vehicle v2 = new Vehicle("BMW", "320i", 2024);
            when(vehicleRepository.findAll()).thenReturn(Arrays.asList(v1, v2));

            // When
            List<Vehicle> result = vehicleService.getAllVehicles();

            // Then
            assertEquals(2, result.size());
            verify(vehicleRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("VS-02: Boş liste dönmeli (araç yoksa)")
        void getAllVehicles_EmptyList_ShouldReturnEmpty() {
            // Given
            when(vehicleRepository.findAll()).thenReturn(Collections.emptyList());

            // When
            List<Vehicle> result = vehicleService.getAllVehicles();

            // Then
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("VS-03: Mevcut araçlar listelenebilmeli")
        void getAvailableVehicles_ShouldReturnOnlyAvailable() {
            // Given
            Vehicle available = new Vehicle("Toyota", "Corolla", 2023);
            available.setAvailable(true);
            when(vehicleRepository.findByAvailableTrue()).thenReturn(List.of(available));

            // When
            List<Vehicle> result = vehicleService.getAvailableVehicles();

            // Then
            assertEquals(1, result.size());
            assertTrue(result.get(0).isAvailable());
        }
    }

    @Nested
    @DisplayName("Araç Getirme Testleri")
    class GetByIdTests {

        @Test
        @DisplayName("VS-04: ID ile araç getirilebilmeli")
        void getVehicleById_ExistingId_ShouldReturnVehicle() {
            // Given
            when(vehicleRepository.findById(1L)).thenReturn(Optional.of(testVehicle));

            // When
            Vehicle result = vehicleService.getVehicleById(1L);

            // Then
            assertNotNull(result);
            assertEquals("Toyota", result.getBrand());
            assertEquals("Corolla", result.getModel());
        }

        @Test
        @DisplayName("VS-05: Olmayan ID için exception fırlatılmalı")
        void getVehicleById_NonExistingId_ShouldThrowException() {
            // Given
            when(vehicleRepository.findById(999L)).thenReturn(Optional.empty());

            // When & Then
            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> vehicleService.getVehicleById(999L));

            assertTrue(exception.getMessage().contains("Araç"));
        }
    }

    @Nested
    @DisplayName("Araç Arama Testleri")
    class SearchTests {

        @Test
        @DisplayName("VS-06: Marka ile arama yapılabilmeli")
        void searchByBrand_ShouldReturnMatchingVehicles() {
            // Given
            when(vehicleRepository.findByBrandContainingIgnoreCase("Toyota"))
                    .thenReturn(List.of(testVehicle));

            // When
            List<Vehicle> result = vehicleService.searchByBrand("Toyota");

            // Then
            assertEquals(1, result.size());
            assertEquals("Toyota", result.get(0).getBrand());
        }

        @Test
        @DisplayName("VS-07: Bulunamayan marka için boş liste dönmeli")
        void searchByBrand_NotFound_ShouldReturnEmpty() {
            // Given
            when(vehicleRepository.findByBrandContainingIgnoreCase("Ferrari"))
                    .thenReturn(Collections.emptyList());

            // When
            List<Vehicle> result = vehicleService.searchByBrand("Ferrari");

            // Then
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Araç CRUD Testleri")
    class CrudTests {

        @Test
        @DisplayName("VS-08: Yeni araç eklenebilmeli")
        void createVehicle_ShouldSaveAndReturn() {
            // Given
            when(vehicleRepository.save(any(Vehicle.class))).thenReturn(testVehicle);

            // When
            Vehicle result = vehicleService.createVehicle(testVehicle);

            // Then
            assertNotNull(result);
            verify(vehicleRepository, times(1)).save(testVehicle);
        }

        @Test
        @DisplayName("VS-09: Araç güncellenebilmeli")
        void updateVehicle_ShouldUpdateAndReturn() {
            // Given
            when(vehicleRepository.findById(1L)).thenReturn(Optional.of(testVehicle));

            Vehicle updateData = new Vehicle("Honda", "Civic", 2024);
            when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            Vehicle result = vehicleService.updateVehicle(1L, updateData);

            // Then
            assertEquals("Honda", result.getBrand());
            assertEquals("Civic", result.getModel());
            assertEquals(2024, result.getYear());
        }

        @Test
        @DisplayName("VS-10: Araç silinebilmeli")
        void deleteVehicle_ShouldDeleteSuccessfully() {
            // Given
            when(vehicleRepository.findById(1L)).thenReturn(Optional.of(testVehicle));
            doNothing().when(vehicleRepository).delete(testVehicle);

            // When
            vehicleService.deleteVehicle(1L);

            // Then
            verify(vehicleRepository, times(1)).delete(testVehicle);
        }

        @Test
        @DisplayName("VS-11: Olmayan araç silinmeye çalışılırsa exception")
        void deleteVehicle_NonExisting_ShouldThrow() {
            // Given
            when(vehicleRepository.findById(999L)).thenReturn(Optional.empty());

            // When & Then
            assertThrows(
                    ResourceNotFoundException.class,
                    () -> vehicleService.deleteVehicle(999L));

            verify(vehicleRepository, never()).delete(any());
        }
    }
}
