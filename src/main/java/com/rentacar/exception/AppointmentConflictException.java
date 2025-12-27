package com.rentacar.exception;

/**
 * Randevu çakışması durumunda fırlatılan exception
 * Aynı araç, aynı tarih, aynı saat için ikinci randevu alınmak istendiğinde
 * kullanılır
 */
public class AppointmentConflictException extends RuntimeException {

    public AppointmentConflictException(String message) {
        super(message);
    }

    public AppointmentConflictException(Long vehicleId, String date, String time) {
        super(String.format("Bu araç için %s tarihinde saat %s zaten rezerve edilmiş.", date, time));
    }
}
