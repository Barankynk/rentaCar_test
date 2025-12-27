package com.rentacar.service;

import com.rentacar.exception.AppointmentConflictException;
import com.rentacar.exception.InvalidTimeException;
import com.rentacar.exception.PastDateException;
import com.rentacar.exception.ResourceNotFoundException;
import com.rentacar.model.Appointment;
import com.rentacar.model.AppointmentStatus;
import com.rentacar.model.Vehicle;
import com.rentacar.repository.AppointmentRepository;
import com.rentacar.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Randevu Service
 * Randevu ile ilgili iş mantığı
 * 
 * BU SINIF TEST SENARYOLARİNİN MERKEZİ!
 * - Çakışma kontrolü
 * - Tarih validasyonu
 * - Saat validasyonu
 */
@Service
@Transactional
public class AppointmentService {

    // İş kuralları: Randevu saatleri
    private static final LocalTime OPENING_TIME = LocalTime.of(9, 0); // 09:00
    private static final LocalTime CLOSING_TIME = LocalTime.of(18, 0); // 18:00

    private final AppointmentRepository appointmentRepository;
    private final VehicleRepository vehicleRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
            VehicleRepository vehicleRepository) {
        this.appointmentRepository = appointmentRepository;
        this.vehicleRepository = vehicleRepository;
    }

    /**
     * Yeni randevu oluştur
     * 
     * Validasyonlar:
     * 1. Geçmiş tarih kontrolü
     * 2. Mesai saati kontrolü (09:00 - 18:00)
     * 3. Çakışma kontrolü (aynı araç, tarih, saat)
     * 
     * @param appointment Randevu bilgileri
     * @return Kaydedilen randevu
     * @throws PastDateException            Geçmiş tarihe randevu alınamaz
     * @throws InvalidTimeException         Mesai saatleri dışında randevu alınamaz
     * @throws AppointmentConflictException Bu slot zaten dolu
     */
    public Appointment createAppointment(Appointment appointment) {
        // 1. Geçmiş tarih kontrolü
        validateDate(appointment.getAppointmentDate());

        // 2. Mesai saati kontrolü
        validateTime(appointment.getAppointmentTime());

        // 3. Çakışma kontrolü
        validateNoConflict(
                appointment.getVehicle().getId(),
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime());

        // Randevuyu kaydet
        appointment.setStatus(AppointmentStatus.PENDING);
        return appointmentRepository.save(appointment);
    }

    /**
     * Randevu oluştur (Araç ID ile)
     */
    public Appointment createAppointment(Long vehicleId, String customerName,
            String customerPhone, String customerEmail,
            LocalDate date, LocalTime time) {
        // Aracı bul
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Araç", vehicleId));

        // Appointment oluştur
        Appointment appointment = new Appointment();
        appointment.setVehicle(vehicle);
        appointment.setCustomerName(customerName);
        appointment.setCustomerPhone(customerPhone);
        appointment.setCustomerEmail(customerEmail);
        appointment.setAppointmentDate(date);
        appointment.setAppointmentTime(time);

        return createAppointment(appointment);
    }

    /**
     * Geçmiş tarih kontrolü
     */
    public void validateDate(LocalDate date) {
        if (date.isBefore(LocalDate.now())) {
            throw new PastDateException("Geçmiş tarihe randevu alınamaz: " + date);
        }
    }

    /**
     * Mesai saati kontrolü (09:00 - 18:00)
     * Boundary Value: 09:00 geçerli, 08:59 geçersiz, 18:00 geçerli, 18:01 geçersiz
     */
    public void validateTime(LocalTime time) {
        if (time.isBefore(OPENING_TIME) || time.isAfter(CLOSING_TIME)) {
            throw new InvalidTimeException(
                    String.format("Randevu saati %s - %s arasında olmalıdır. Girilen: %s",
                            OPENING_TIME, CLOSING_TIME, time));
        }
    }

    /**
     * Çakışma kontrolü
     * Aynı araç, aynı tarih, aynı saat için başka randevu var mı?
     */
    public void validateNoConflict(Long vehicleId, LocalDate date, LocalTime time) {
        boolean exists = appointmentRepository
                .existsByVehicleIdAndAppointmentDateAndAppointmentTime(vehicleId, date, time);

        if (exists) {
            throw new AppointmentConflictException(vehicleId, date.toString(), time.toString());
        }
    }

    /**
     * Tüm randevuları listele
     */
    @Transactional(readOnly = true)
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    /**
     * ID ile randevu getir
     */
    @Transactional(readOnly = true)
    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Randevu", id));
    }

    /**
     * Müşteri telefonuna göre randevuları getir
     */
    @Transactional(readOnly = true)
    public List<Appointment> getAppointmentsByPhone(String phone) {
        return appointmentRepository.findByCustomerPhone(phone);
    }

    /**
     * Araç için dolu saatleri getir (belirli tarihte)
     */
    @Transactional(readOnly = true)
    public List<LocalTime> getBookedTimesForVehicle(Long vehicleId, LocalDate date) {
        return appointmentRepository.findByVehicleIdAndAppointmentDate(vehicleId, date)
                .stream()
                .map(Appointment::getAppointmentTime)
                .toList();
    }

    /**
     * Randevu iptal et
     */
    public Appointment cancelAppointment(Long id) {
        Appointment appointment = getAppointmentById(id);
        appointment.setStatus(AppointmentStatus.CANCELLED);
        return appointmentRepository.save(appointment);
    }

    /**
     * Randevu onayla
     */
    public Appointment confirmAppointment(Long id) {
        Appointment appointment = getAppointmentById(id);
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        return appointmentRepository.save(appointment);
    }

    /**
     * Bugünkü randevuları getir
     */
    @Transactional(readOnly = true)
    public List<Appointment> getTodayAppointments() {
        return appointmentRepository.findTodayAppointments();
    }

    /**
     * Aktif (iptal edilmemiş) randevuları getir
     */
    @Transactional(readOnly = true)
    public List<Appointment> getActiveAppointments() {
        return appointmentRepository.findActiveAppointments(AppointmentStatus.CANCELLED);
    }

    /**
     * Duruma göre randevuları getir
     */
    @Transactional(readOnly = true)
    public List<Appointment> getAppointmentsByStatus(AppointmentStatus status) {
        return appointmentRepository.findByStatus(status);
    }
}
