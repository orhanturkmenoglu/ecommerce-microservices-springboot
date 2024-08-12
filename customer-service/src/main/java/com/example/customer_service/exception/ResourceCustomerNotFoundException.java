package com.example.customer_service.exception;

public class ResourceCustomerNotFoundException extends RuntimeException {
    public ResourceCustomerNotFoundException(String message) {
        super(message);
    }
}
