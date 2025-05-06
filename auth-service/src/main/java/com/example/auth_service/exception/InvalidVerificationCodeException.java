package com.example.auth_service.exception;

public class InvalidVerificationCodeException extends RuntimeException{

    public InvalidVerificationCodeException(String message) {
        super(message);
    }
}
