package com.rentacar.exception;

/**
 * Kaynak bulunamadığında fırlatılan exception
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, Long id) {
        super(String.format("%s bulunamadı. ID: %d", resourceName, id));
    }
}
