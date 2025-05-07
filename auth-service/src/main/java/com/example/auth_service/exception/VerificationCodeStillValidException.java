package com.example.auth_service.exception;

public class VerificationCodeStillValidException extends RuntimeException {
    public VerificationCodeStillValidException(String message) {
        super(message);
    }
}
