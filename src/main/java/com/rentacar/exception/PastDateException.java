package com.rentacar.exception;

/**
 * Geçmiş tarihe randevu alınmak istendiğinde fırlatılan exception
 */
public class PastDateException extends RuntimeException {

    public PastDateException(String message) {
        super(message);
    }

    public PastDateException() {
        super("Geçmiş tarihe randevu alınamaz.");
    }
}
