package com.rentacar.integration;

import com.rentacar.exception.AppointmentConflictException;
import com.rentacar.exception.InvalidTimeException;
import com.rentacar.exception.PastDateException;
import com.rentacar.model.Appointment;
import com.rentacar.model.AppointmentStatus;
import com.rentacar.model.Vehicle;
import com.rentacar.repository.VehicleRepository;
import com.rentacar.service.AppointmentService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Service + Repository Entegrasyon Testleri
 * 
 * Test Seviyesi: INTEGRATION TEST
 * Full Spring context ile real database testi
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Service Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ServiceIntegrationTest {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private VehicleRepository vehicleRepository;

    private Vehicle testVehicle;

    @BeforeEach
    void setUp() {
        // Test aracı oluştur
        testVehicle = new Vehicle("Integration", "Test", 2024);
        testVehicle = vehicleRepository.save(testVehicle);
    }

    @Test
    @Order(1)
    @DisplayName("SIT-01: Full flow - Randevu oluşturma ve kaydetme")
    void createAppointment_FullFlow_ShouldPersist() {
        // Given
        LocalDate date = LocalDate.now().plusDays(1);
        LocalTime time = LocalTime.of(10, 0);

        // When
        Appointment result = appointmentService.createAppointment(
                testVehicle.getId(),
                "Integration Test User",
                "05559999999",
                "test@integration.com",
                date,
                time);

        // Then
        assertNotNull(result.getId());
        assertEquals("Integration Test User", result.getCustomerName());
        assertEquals(AppointmentStatus.PENDING, result.getStatus());

        // Verify persisted
        Appointment retrieved = appointmentService.getAppointmentById(result.getId());
        assertEquals(result.getId(), retrieved.getId());
    }

    @Test
    @Order(2)
    @DisplayName("SIT-02: Çakışma senaryosu - Aynı slot ikinci kez alınamamalı")
    void createAppointment_ConflictScenario_ShouldFail() {
        // Given
        LocalDate date = LocalDate.now().plusDays(2);
        LocalTime time = LocalTime.of(11, 0);

        // İlk randevuyu oluştur
        appointmentService.createAppointment(
                testVehicle.getId(),
                "First Customer",
                "05551111111",
                null,
                date,
                time);

        // When & Then - Aynı slot için ikinci randevu
        assertThrows(AppointmentConflictException.class, () -> appointmentService.createAppointment(
                testVehicle.getId(),
                "Second Customer",
                "05552222222",
                null,
                date,
                time));
    }

    @Test
    @Order(3)
    @DisplayName("SIT-03: Aynı araç farklı saatlerde randevu alınabilmeli")
    void createAppointment_DifferentTimes_ShouldSucceed() {
        // Given
        LocalDate date = LocalDate.now().plusDays(3);

        // When
        Appointment apt1 = appointmentService.createAppointment(
                testVehicle.getId(), "User1", "05551111111", null,
                date, LocalTime.of(9, 0));

        Appointment apt2 = appointmentService.createAppointment(
                testVehicle.getId(), "User2", "05552222222", null,
                date, LocalTime.of(14, 0));

        // Then
        assertNotNull(apt1.getId());
        assertNotNull(apt2.getId());
        assertNotEquals(apt1.getId(), apt2.getId());
    }

    @Test
    @Order(4)
    @DisplayName("SIT-04: Geçmiş tarih validasyonu")
    void createAppointment_PastDate_ShouldThrow() {
        // Given
        LocalDate pastDate = LocalDate.now().minusDays(1);
        LocalTime time = LocalTime.of(10, 0);

        // When & Then
        assertThrows(PastDateException.class, () -> appointmentService.createAppointment(
                testVehicle.getId(),
                "Test User",
                "05551234567",
                null,
                pastDate,
                time));
    }

    @Test
    @Order(5)
    @DisplayName("SIT-05: Mesai dışı saat validasyonu")
    void createAppointment_OutsideBusinessHours_ShouldThrow() {
        // Given
        LocalDate date = LocalDate.now().plusDays(4);
        LocalTime earlyTime = LocalTime.of(7, 0);

        // When & Then
        assertThrows(InvalidTimeException.class, () -> appointmentService.createAppointment(
                testVehicle.getId(),
                "Test User",
                "05551234567",
                null,
                date,
                earlyTime));
    }

    @Test
    @Order(6)
    @DisplayName("SIT-06: Randevu iptal akışı")
    void cancelAppointment_ShouldUpdateStatus() {
        // Given
        LocalDate date = LocalDate.now().plusDays(5);
        Appointment created = appointmentService.createAppointment(
                testVehicle.getId(), "Cancel Test", "05553333333", null,
                date, LocalTime.of(15, 0));

        // When
        Appointment cancelled = appointmentService.cancelAppointment(created.getId());

        // Then
        assertEquals(AppointmentStatus.CANCELLED, cancelled.getStatus());

        // Verify in DB
        Appointment retrieved = appointmentService.getAppointmentById(created.getId());
        assertEquals(AppointmentStatus.CANCELLED, retrieved.getStatus());
    }

    @Test
    @Order(7)
    @DisplayName("SIT-07: Telefon ile randevu arama")
    void getAppointmentsByPhone_ShouldReturnCorrectList() {
        // Given
        String phone = "05554444444";
        LocalDate date = LocalDate.now().plusDays(6);

        appointmentService.createAppointment(
                testVehicle.getId(), "Phone Test 1", phone, null,
                date, LocalTime.of(10, 0));
        appointmentService.createAppointment(
                testVehicle.getId(), "Phone Test 2", phone, null,
                date, LocalTime.of(11, 0));

        // When
        List<Appointment> result = appointmentService.getAppointmentsByPhone(phone);

        // Then
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(a -> a.getCustomerPhone().equals(phone)));
    }

    @Test
    @Order(8)
    @DisplayName("SIT-08: Dolu saatleri getirme")
    void getBookedTimesForVehicle_ShouldReturnBookedSlots() {
        // Given
        LocalDate date = LocalDate.now().plusDays(7);

        appointmentService.createAppointment(
                testVehicle.getId(), "Slot1", "05551111111", null,
                date, LocalTime.of(9, 0));
        appointmentService.createAppointment(
                testVehicle.getId(), "Slot2", "05552222222", null,
                date, LocalTime.of(12, 0));
        appointmentService.createAppointment(
                testVehicle.getId(), "Slot3", "05553333333", null,
                date, LocalTime.of(16, 0));

        // When
        List<LocalTime> bookedTimes = appointmentService.getBookedTimesForVehicle(
                testVehicle.getId(), date);

        // Then
        assertEquals(3, bookedTimes.size());
        assertTrue(bookedTimes.contains(LocalTime.of(9, 0)));
        assertTrue(bookedTimes.contains(LocalTime.of(12, 0)));
        assertTrue(bookedTimes.contains(LocalTime.of(16, 0)));
    }
}
