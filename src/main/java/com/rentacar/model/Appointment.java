package com.rentacar.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

/**
 * Randevu entity sınıfı
 * Test sürüşü randevularını temsil eder
 */
@Entity
@Table(name = "appointments", uniqueConstraints = @UniqueConstraint(columnNames = { "vehicle_id", "appointment_date",
        "appointment_time" }, name = "uk_vehicle_date_time"))
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    @NotNull(message = "Araç seçimi zorunludur")
    private Vehicle vehicle;

    @NotBlank(message = "Ad Soyad boş olamaz")
    @Size(min = 2, max = 100, message = "Ad Soyad 2-100 karakter arası olmalı")
    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @NotBlank(message = "Telefon numarası zorunludur")
    @Pattern(regexp = "^[0-9]{10,11}$", message = "Geçerli bir telefon numarası giriniz (10-11 haneli)")
    @Column(name = "customer_phone", nullable = false)
    private String customerPhone;

    @Email(message = "Geçerli bir e-posta adresi giriniz")
    @Column(name = "customer_email")
    private String customerEmail;

    @NotNull(message = "Randevu tarihi zorunludur")
    @FutureOrPresent(message = "Geçmiş tarihe randevu alınamaz")
    @Column(name = "appointment_date", nullable = false)
    private LocalDate appointmentDate;

    @NotNull(message = "Randevu saati zorunludur")
    @Column(name = "appointment_time", nullable = false)
    private LocalTime appointmentTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status = AppointmentStatus.PENDING;

    @Column(length = 500)
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Constructors
    public Appointment() {
    }

    public Appointment(Vehicle vehicle, String customerName, String customerPhone,
            LocalDate appointmentDate, LocalTime appointmentTime) {
        this.vehicle = vehicle;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public LocalTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "id=" + id +
                ", customerName='" + customerName + '\'' +
                ", appointmentDate=" + appointmentDate +
                ", appointmentTime=" + appointmentTime +
                ", status=" + status +
                '}';
    }
}
