package com.example.payment_service.exception;

public class PaymentCustomerNotFoundException extends RuntimeException {
    public PaymentCustomerNotFoundException(String message) {
        super(message);
    }
}
