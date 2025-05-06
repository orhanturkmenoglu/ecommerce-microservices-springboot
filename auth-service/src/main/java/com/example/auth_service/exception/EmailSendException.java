package com.example.auth_service.exception;

public class EmailSendException extends RuntimeException {

    public EmailSendException(String message) {
        super(message);
    }
}
