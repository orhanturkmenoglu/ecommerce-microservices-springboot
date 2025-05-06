package com.example.cargo_service.exception;

public class EmailAlreadyVerifiedException extends RuntimeException {

    public EmailAlreadyVerifiedException(String message) {
        super(message);
    }
}
