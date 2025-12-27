package com.rentacar.exception;

/**
 * Mesai saatleri dışında randevu alınmak istendiğinde fırlatılan exception
 * İş kuralı: Randevular 09:00 - 18:00 arası olmalı
 */
public class InvalidTimeException extends RuntimeException {

    public InvalidTimeException(String message) {
        super(message);
    }

    public InvalidTimeException() {
        super("Randevu saati 09:00 - 18:00 arasında olmalıdır.");
    }
}
