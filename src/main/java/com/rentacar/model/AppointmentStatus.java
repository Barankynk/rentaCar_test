package com.rentacar.model;

/**
 * Randevu durumu enum'u
 */
public enum AppointmentStatus {
    PENDING("Beklemede"),
    CONFIRMED("Onaylandı"),
    CANCELLED("İptal Edildi"),
    COMPLETED("Tamamlandı");

    private final String displayName;

    AppointmentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
