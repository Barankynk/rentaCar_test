package com.rentacar.repository;

import com.rentacar.model.Appointment;
import com.rentacar.model.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Randevu Repository
 * Appointment entity için veritabanı işlemleri
 */
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    /**
     * Çakışma kontrolü: Aynı araç, aynı tarih, aynı saat için randevu var mı?
     */
    boolean existsByVehicleIdAndAppointmentDateAndAppointmentTime(
            Long vehicleId,
            LocalDate appointmentDate,
            LocalTime appointmentTime);

    /**
     * Müşteri e-postasına göre randevuları getir
     */
    List<Appointment> findByCustomerEmail(String customerEmail);

    /**
     * Müşteri telefonuna göre randevuları getir
     */
    List<Appointment> findByCustomerPhone(String customerPhone);

    /**
     * Araç ID'sine göre randevuları getir
     */
    List<Appointment> findByVehicleId(Long vehicleId);

    /**
     * Duruma göre randevuları getir
     */
    List<Appointment> findByStatus(AppointmentStatus status);

    /**
     * Belirli tarihteki randevuları getir
     */
    List<Appointment> findByAppointmentDate(LocalDate date);

    /**
     * Tarih aralığındaki randevuları getir
     */
    List<Appointment> findByAppointmentDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Araç ve tarihe göre randevuları getir (dolu saatleri bulmak için)
     */
    List<Appointment> findByVehicleIdAndAppointmentDate(Long vehicleId, LocalDate date);

    /**
     * İptal edilmemiş aktif randevuları getir
     */
    @Query("SELECT a FROM Appointment a WHERE a.status != :cancelledStatus ORDER BY a.appointmentDate, a.appointmentTime")
    List<Appointment> findActiveAppointments(@Param("cancelledStatus") AppointmentStatus cancelledStatus);

    /**
     * Bugünkü randevuları getir
     */
    @Query("SELECT a FROM Appointment a WHERE a.appointmentDate = CURRENT_DATE ORDER BY a.appointmentTime")
    List<Appointment> findTodayAppointments();
}
