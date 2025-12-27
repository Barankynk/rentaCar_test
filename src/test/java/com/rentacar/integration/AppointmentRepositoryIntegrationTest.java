package com.rentacar.integration;

import com.rentacar.model.Appointment;
import com.rentacar.model.AppointmentStatus;
import com.rentacar.model.Vehicle;
import com.rentacar.repository.AppointmentRepository;
import com.rentacar.repository.VehicleRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AppointmentRepository Integration Testleri
 * 
 * Test Seviyesi: INTEGRATION TEST
 * Real database (H2) ile repository testleri
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("AppointmentRepository Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AppointmentRepositoryIntegrationTest {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    private Vehicle testVehicle;

    @BeforeEach
    void setUp() {
        // Test öncesi temizlik
        appointmentRepository.deleteAll();
        vehicleRepository.deleteAll();

        // Test aracı oluştur
        testVehicle = new Vehicle("Toyota", "Corolla", 2023);
        testVehicle = vehicleRepository.save(testVehicle);
    }

    @Test
    @Order(1)
    @DisplayName("IT-01: Randevu kaydedilip getirilebilmeli")
    void save_ShouldPersistAppointment() {
        // Given
        Appointment appointment = createTestAppointment(
                LocalDate.now().plusDays(1),
                LocalTime.of(10, 0));

        // When
        Appointment saved = appointmentRepository.save(appointment);

        // Then
        assertNotNull(saved.getId());
        assertEquals("Test Müşteri", saved.getCustomerName());
    }

    @Test
    @Order(2)
    @DisplayName("IT-02: Çakışma kontrolü - Aynı slot doluysa true dönmeli")
    void existsByVehicleIdAndDateAndTime_WhenExists_ShouldReturnTrue() {
        // Given
        LocalDate date = LocalDate.now().plusDays(2);
        LocalTime time = LocalTime.of(14, 0);

        Appointment existing = createTestAppointment(date, time);
        appointmentRepository.save(existing);

        // When
        boolean exists = appointmentRepository.existsByVehicleIdAndAppointmentDateAndAppointmentTime(
                testVehicle.getId(), date, time);

        // Then
        assertTrue(exists);
    }

    @Test
    @Order(3)
    @DisplayName("IT-03: Çakışma kontrolü - Farklı slot boşsa false dönmeli")
    void existsByVehicleIdAndDateAndTime_WhenNotExists_ShouldReturnFalse() {
        // Given
        LocalDate date = LocalDate.now().plusDays(3);
        LocalTime time = LocalTime.of(15, 0);

        // When
        boolean exists = appointmentRepository.existsByVehicleIdAndAppointmentDateAndAppointmentTime(
                testVehicle.getId(), date, time);

        // Then
        assertFalse(exists);
    }

    @Test
    @Order(4)
    @DisplayName("IT-04: Müşteri telefonuna göre randevu arama")
    void findByCustomerPhone_ShouldReturnMatchingAppointments() {
        // Given
        Appointment apt1 = createTestAppointment(LocalDate.now().plusDays(1), LocalTime.of(10, 0));
        apt1.setCustomerPhone("05551111111");
        appointmentRepository.save(apt1);

        Appointment apt2 = createTestAppointment(LocalDate.now().plusDays(2), LocalTime.of(11, 0));
        apt2.setCustomerPhone("05551111111");
        appointmentRepository.save(apt2);

        Appointment apt3 = createTestAppointment(LocalDate.now().plusDays(3), LocalTime.of(12, 0));
        apt3.setCustomerPhone("05552222222");
        appointmentRepository.save(apt3);

        // When
        List<Appointment> result = appointmentRepository.findByCustomerPhone("05551111111");

        // Then
        assertEquals(2, result.size());
    }

    @Test
    @Order(5)
    @DisplayName("IT-05: Duruma göre randevu arama")
    void findByStatus_ShouldReturnMatchingAppointments() {
        // Given
        Appointment pending = createTestAppointment(LocalDate.now().plusDays(1), LocalTime.of(10, 0));
        pending.setStatus(AppointmentStatus.PENDING);
        appointmentRepository.save(pending);

        Appointment confirmed = createTestAppointment(LocalDate.now().plusDays(2), LocalTime.of(11, 0));
        confirmed.setStatus(AppointmentStatus.CONFIRMED);
        appointmentRepository.save(confirmed);

        // When
        List<Appointment> pendingList = appointmentRepository.findByStatus(AppointmentStatus.PENDING);
        List<Appointment> confirmedList = appointmentRepository.findByStatus(AppointmentStatus.CONFIRMED);

        // Then
        assertEquals(1, pendingList.size());
        assertEquals(1, confirmedList.size());
    }

    @Test
    @Order(6)
    @DisplayName("IT-06: Tarihe göre randevu arama")
    void findByAppointmentDate_ShouldReturnMatchingAppointments() {
        // Given
        LocalDate targetDate = LocalDate.now().plusDays(5);

        Appointment apt1 = createTestAppointment(targetDate, LocalTime.of(10, 0));
        appointmentRepository.save(apt1);

        Appointment apt2 = createTestAppointment(targetDate, LocalTime.of(14, 0));
        appointmentRepository.save(apt2);

        // When
        List<Appointment> result = appointmentRepository.findByAppointmentDate(targetDate);

        // Then
        assertEquals(2, result.size());
    }

    @Test
    @Order(7)
    @DisplayName("IT-07: Araç ve tarihe göre randevu arama (dolu saatler)")
    void findByVehicleIdAndAppointmentDate_ShouldReturnBookedSlots() {
        // Given
        LocalDate date = LocalDate.now().plusDays(6);

        Appointment apt1 = createTestAppointment(date, LocalTime.of(9, 0));
        Appointment apt2 = createTestAppointment(date, LocalTime.of(11, 0));
        Appointment apt3 = createTestAppointment(date, LocalTime.of(15, 0));

        appointmentRepository.save(apt1);
        appointmentRepository.save(apt2);
        appointmentRepository.save(apt3);

        // When
        List<Appointment> result = appointmentRepository.findByVehicleIdAndAppointmentDate(
                testVehicle.getId(), date);

        // Then
        assertEquals(3, result.size());
    }

    @Test
    @Order(8)
    @DisplayName("IT-08: Tarih aralığında randevu arama")
    void findByAppointmentDateBetween_ShouldReturnAppointmentsInRange() {
        // Given
        LocalDate start = LocalDate.now().plusDays(10);
        LocalDate end = LocalDate.now().plusDays(15);

        appointmentRepository.save(createTestAppointment(start.plusDays(1), LocalTime.of(10, 0)));
        appointmentRepository.save(createTestAppointment(start.plusDays(2), LocalTime.of(11, 0)));
        appointmentRepository.save(createTestAppointment(end.plusDays(5), LocalTime.of(12, 0))); // Aralık dışı

        // When
        List<Appointment> result = appointmentRepository.findByAppointmentDateBetween(start, end);

        // Then
        assertEquals(2, result.size());
    }

    // Helper method
    private Appointment createTestAppointment(LocalDate date, LocalTime time) {
        Appointment appointment = new Appointment();
        appointment.setVehicle(testVehicle);
        appointment.setCustomerName("Test Müşteri");
        appointment.setCustomerPhone("05551234567");
        appointment.setAppointmentDate(date);
        appointment.setAppointmentTime(time);
        appointment.setStatus(AppointmentStatus.PENDING);
        return appointment;
    }
}
