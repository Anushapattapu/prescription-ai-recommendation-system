package com.prescription.backend.exception;

public class UnclearPrescriptionException extends RuntimeException {
    public UnclearPrescriptionException(String message) {
        super(message);
    }
}
