package com.rentacar.unit;

import com.rentacar.exception.AppointmentConflictException;
import com.rentacar.exception.InvalidTimeException;
import com.rentacar.exception.PastDateException;
import com.rentacar.exception.ResourceNotFoundException;
import com.rentacar.model.Appointment;
import com.rentacar.model.AppointmentStatus;
import com.rentacar.model.Vehicle;
import com.rentacar.repository.AppointmentRepository;
import com.rentacar.repository.VehicleRepository;
import com.rentacar.service.AppointmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * AppointmentService Unit Testleri
 * 
 * Test Seviyeleri: UNIT TEST
 * Test Teknikleri:
 * - Positive Testing (geçerli senaryolar)
 * - Negative Testing (hata senaryoları)
 * - Boundary Value Analysis (sınır değerleri)
 * - Equivalence Partitioning (eşdeğer sınıflar)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AppointmentService Unit Tests")
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    private Vehicle testVehicle;
    private Appointment testAppointment;

    @BeforeEach
    void setUp() {
        // Test verilerini hazırla
        testVehicle = new Vehicle("Toyota", "Corolla", 2023);
        testVehicle.setId(1L);

        testAppointment = new Appointment();
        testAppointment.setVehicle(testVehicle);
        testAppointment.setCustomerName("Test Müşteri");
        testAppointment.setCustomerPhone("05551234567");
        testAppointment.setAppointmentDate(LocalDate.now().plusDays(1));
        testAppointment.setAppointmentTime(LocalTime.of(10, 0));
    }

    // ==================== POSITIVE TESTS ====================

    @Nested
    @DisplayName("Pozitif Test Senaryoları")
    class PositiveTests {

        @Test
        @DisplayName("UT-01: Geçerli randevu başarıyla oluşturulmalı")
        void createAppointment_ValidData_ShouldSucceed() {
            // Given
            when(appointmentRepository.existsByVehicleIdAndAppointmentDateAndAppointmentTime(
                    anyLong(), any(LocalDate.class), any(LocalTime.class)))
                    .thenReturn(false);
            when(appointmentRepository.save(any(Appointment.class)))
                    .thenReturn(testAppointment);

            // When
            Appointment result = appointmentService.createAppointment(testAppointment);

            // Then
            assertNotNull(result);
            assertEquals(AppointmentStatus.PENDING, result.getStatus());
            verify(appointmentRepository, times(1)).save(any(Appointment.class));
        }

        @Test
        @DisplayName("UT-02: Tüm randevular listelenebilmeli")
        void getAllAppointments_ShouldReturnList() {
            // Given
            Appointment apt1 = new Appointment();
            Appointment apt2 = new Appointment();
            when(appointmentRepository.findAll()).thenReturn(Arrays.asList(apt1, apt2));

            // When
            List<Appointment> result = appointmentService.getAllAppointments();

            // Then
            assertEquals(2, result.size());
            verify(appointmentRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("UT-03: ID ile randevu getirilebilmeli")
        void getAppointmentById_ExistingId_ShouldReturn() {
            // Given
            testAppointment.setId(1L);
            when(appointmentRepository.findById(1L)).thenReturn(Optional.of(testAppointment));

            // When
            Appointment result = appointmentService.getAppointmentById(1L);

            // Then
            assertNotNull(result);
            assertEquals(1L, result.getId());
        }

        @Test
        @DisplayName("UT-04: Randevu iptal edilebilmeli")
        void cancelAppointment_ShouldChangeStatus() {
            // Given
            testAppointment.setId(1L);
            testAppointment.setStatus(AppointmentStatus.PENDING);
            when(appointmentRepository.findById(1L)).thenReturn(Optional.of(testAppointment));
            when(appointmentRepository.save(any(Appointment.class))).thenReturn(testAppointment);

            // When
            Appointment result = appointmentService.cancelAppointment(1L);

            // Then
            assertEquals(AppointmentStatus.CANCELLED, result.getStatus());
        }

        @Test
        @DisplayName("UT-05: Randevu onaylanabilmeli")
        void confirmAppointment_ShouldChangeStatus() {
            // Given
            testAppointment.setId(1L);
            testAppointment.setStatus(AppointmentStatus.PENDING);
            when(appointmentRepository.findById(1L)).thenReturn(Optional.of(testAppointment));
            when(appointmentRepository.save(any(Appointment.class))).thenReturn(testAppointment);

            // When
            Appointment result = appointmentService.confirmAppointment(1L);

            // Then
            assertEquals(AppointmentStatus.CONFIRMED, result.getStatus());
        }
    }

    // ==================== NEGATIVE TESTS ====================

    @Nested
    @DisplayName("Negatif Test Senaryoları")
    class NegativeTests {

        @Test
        @DisplayName("UT-06: Geçmiş tarihe randevu oluşturulamamalı - PastDateException")
        void createAppointment_PastDate_ShouldThrowException() {
            // Given
            testAppointment.setAppointmentDate(LocalDate.now().minusDays(1));

            // When & Then
            PastDateException exception = assertThrows(
                    PastDateException.class,
                    () -> appointmentService.createAppointment(testAppointment));

            assertTrue(exception.getMessage().contains("Geçmiş tarihe"));
            verify(appointmentRepository, never()).save(any());
        }

        @Test
        @DisplayName("UT-07: Çakışan randevu oluşturulamamalı - AppointmentConflictException")
        void createAppointment_ConflictingTime_ShouldThrowException() {
            // Given
            when(appointmentRepository.existsByVehicleIdAndAppointmentDateAndAppointmentTime(
                    anyLong(), any(LocalDate.class), any(LocalTime.class)))
                    .thenReturn(true); // Çakışma var!

            // When & Then
            AppointmentConflictException exception = assertThrows(
                    AppointmentConflictException.class,
                    () -> appointmentService.createAppointment(testAppointment));

            assertTrue(exception.getMessage().contains("zaten rezerve"));
            verify(appointmentRepository, never()).save(any());
        }

        @Test
        @DisplayName("UT-08: Olmayan ID ile randevu getirme - ResourceNotFoundException")
        void getAppointmentById_NonExistingId_ShouldThrowException() {
            // Given
            when(appointmentRepository.findById(999L)).thenReturn(Optional.empty());

            // When & Then
            assertThrows(
                    ResourceNotFoundException.class,
                    () -> appointmentService.getAppointmentById(999L));
        }
    }

    // ==================== BOUNDARY VALUE ANALYSIS ====================

    @Nested
    @DisplayName("Sınır Değer Analizi (BVA) - Saat Kontrolü")
    class BoundaryValueTests {

        @Test
        @DisplayName("BVA-01: Saat 09:00 geçerli olmalı (alt sınır)")
        void validateTime_At0900_ShouldPass() {
            // Given
            LocalTime time = LocalTime.of(9, 0);

            // When & Then
            assertDoesNotThrow(() -> appointmentService.validateTime(time));
        }

        @Test
        @DisplayName("BVA-02: Saat 18:00 geçerli olmalı (üst sınır)")
        void validateTime_At1800_ShouldPass() {
            // Given
            LocalTime time = LocalTime.of(18, 0);

            // When & Then
            assertDoesNotThrow(() -> appointmentService.validateTime(time));
        }

        @Test
        @DisplayName("BVA-03: Saat 08:59 geçersiz olmalı (alt sınır altı)")
        void validateTime_At0859_ShouldFail() {
            // Given
            LocalTime time = LocalTime.of(8, 59);

            // When & Then
            InvalidTimeException exception = assertThrows(
                    InvalidTimeException.class,
                    () -> appointmentService.validateTime(time));

            assertTrue(exception.getMessage().contains("09:00"));
        }

        @Test
        @DisplayName("BVA-04: Saat 18:01 geçersiz olmalı (üst sınır üstü)")
        void validateTime_At1801_ShouldFail() {
            // Given
            LocalTime time = LocalTime.of(18, 1);

            // When & Then
            InvalidTimeException exception = assertThrows(
                    InvalidTimeException.class,
                    () -> appointmentService.validateTime(time));

            assertTrue(exception.getMessage().contains("18:00"));
        }

        @Test
        @DisplayName("BVA-05: Saat 12:00 geçerli olmalı (orta değer)")
        void validateTime_At1200_ShouldPass() {
            // Given
            LocalTime time = LocalTime.of(12, 0);

            // When & Then
            assertDoesNotThrow(() -> appointmentService.validateTime(time));
        }
    }

    // ==================== EQUIVALENCE PARTITIONING ====================

    @Nested
    @DisplayName("Eşdeğer Sınıf Bölmeleme (EP) - Tarih Kontrolü")
    class EquivalencePartitionTests {

        @Test
        @DisplayName("EP-01: Geçerli Sınıf - Gelecek tarih kabul edilmeli")
        void validateDate_FutureDate_ShouldPass() {
            // Given
            LocalDate futureDate = LocalDate.now().plusDays(7);

            // When & Then
            assertDoesNotThrow(() -> appointmentService.validateDate(futureDate));
        }

        @Test
        @DisplayName("EP-02: Geçerli Sınıf - Bugün kabul edilmeli")
        void validateDate_Today_ShouldPass() {
            // Given
            LocalDate today = LocalDate.now();

            // When & Then
            assertDoesNotThrow(() -> appointmentService.validateDate(today));
        }

        @Test
        @DisplayName("EP-03: Geçersiz Sınıf - Dün reddedilmeli")
        void validateDate_Yesterday_ShouldFail() {
            // Given
            LocalDate yesterday = LocalDate.now().minusDays(1);

            // When & Then
            PastDateException exception = assertThrows(
                    PastDateException.class,
                    () -> appointmentService.validateDate(yesterday));

            assertNotNull(exception.getMessage());
        }

        @Test
        @DisplayName("EP-04: Geçersiz Sınıf - Geçmiş hafta reddedilmeli")
        void validateDate_LastWeek_ShouldFail() {
            // Given
            LocalDate lastWeek = LocalDate.now().minusWeeks(1);

            // When & Then
            assertThrows(
                    PastDateException.class,
                    () -> appointmentService.validateDate(lastWeek));
        }
    }

    // ==================== CONFLICT TESTS ====================

    @Nested
    @DisplayName("Çakışma Kontrolü Testleri")
    class ConflictTests {

        @Test
        @DisplayName("CONF-01: Aynı araç farklı tarih - Çakışma olmamalı")
        void validateNoConflict_SameVehicleDifferentDate_ShouldPass() {
            // Given
            when(appointmentRepository.existsByVehicleIdAndAppointmentDateAndAppointmentTime(
                    anyLong(), any(LocalDate.class), any(LocalTime.class)))
                    .thenReturn(false);

            // When & Then
            assertDoesNotThrow(
                    () -> appointmentService.validateNoConflict(1L, LocalDate.now().plusDays(1), LocalTime.of(10, 0)));
        }

        @Test
        @DisplayName("CONF-02: Aynı araç aynı tarih farklı saat - Çakışma olmamalı")
        void validateNoConflict_SameVehicleSameDateDifferentTime_ShouldPass() {
            // Given
            when(appointmentRepository.existsByVehicleIdAndAppointmentDateAndAppointmentTime(
                    anyLong(), any(LocalDate.class), any(LocalTime.class)))
                    .thenReturn(false);

            // When & Then
            assertDoesNotThrow(
                    () -> appointmentService.validateNoConflict(1L, LocalDate.now().plusDays(1), LocalTime.of(14, 0)));
        }

        @Test
        @DisplayName("CONF-03: Aynı araç aynı tarih aynı saat - Çakışma olmalı")
        void validateNoConflict_ExactMatch_ShouldThrow() {
            // Given
            when(appointmentRepository.existsByVehicleIdAndAppointmentDateAndAppointmentTime(
                    eq(1L), any(LocalDate.class), eq(LocalTime.of(10, 0))))
                    .thenReturn(true);

            // When & Then
            assertThrows(
                    AppointmentConflictException.class,
                    () -> appointmentService.validateNoConflict(1L, LocalDate.now().plusDays(1), LocalTime.of(10, 0)));
        }
    }
}
