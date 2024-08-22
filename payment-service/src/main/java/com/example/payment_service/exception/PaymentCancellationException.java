package com.example.payment_service.exception;

public class PaymentCancellationException extends RuntimeException {
    public PaymentCancellationException(String message) {
        super(message);
    }
}
