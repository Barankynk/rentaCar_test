package com.rentacar.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Randevu oluşturma formu için DTO
 */
public class AppointmentForm {

    @NotNull(message = "Araç seçimi zorunludur")
    private Long vehicleId;

    @NotBlank(message = "Ad Soyad boş olamaz")
    @Size(min = 2, max = 100, message = "Ad Soyad 2-100 karakter arası olmalı")
    private String customerName;

    @NotBlank(message = "Telefon numarası zorunludur")
    @Pattern(regexp = "^[0-9]{10,11}$", message = "Geçerli bir telefon numarası giriniz (10-11 haneli)")
    private String customerPhone;

    @Email(message = "Geçerli bir e-posta adresi giriniz")
    private String customerEmail;

    @NotNull(message = "Randevu tarihi zorunludur")
    @FutureOrPresent(message = "Geçmiş tarihe randevu alınamaz")
    private LocalDate appointmentDate;

    @NotNull(message = "Randevu saati zorunludur")
    private LocalTime appointmentTime;

    private String notes;

    // Getters and Setters
    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
