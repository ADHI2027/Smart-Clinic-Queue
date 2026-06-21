package com.smartclinic.exception;

public class PatientNotFoundException extends RuntimeException {
    public PatientNotFoundException(String message) {
        super(message);
    }
    
    public PatientNotFoundException(String id, String token) {
        super(String.format("Patient with id '%s' or token '%s' not found", id, token));
    }
}