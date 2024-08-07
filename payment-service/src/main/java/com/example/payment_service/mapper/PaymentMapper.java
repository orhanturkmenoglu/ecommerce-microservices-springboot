package com.example.payment_service.mapper;

import com.example.payment_service.dto.paymentDto.PaymentRequestDto;
import com.example.payment_service.dto.paymentDto.PaymentResponseDto;
import com.example.payment_service.enums.PaymentStatus;
import com.example.payment_service.model.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public Payment mapToPayment(PaymentRequestDto paymentRequestDto) {
        return Payment.builder()
                .orderId(paymentRequestDto.getOrderId())
                .paymentStatus(PaymentStatus.COMPLETED)
                .paymentType(paymentRequestDto.getPaymentType())
                .build();
    }

    public PaymentResponseDto mapToPaymentResponseDto(Payment payment) {
        return PaymentResponseDto.builder()
                .id(payment.getId())
                .orderId(payment.getOrderId())
                .paymentStatus(payment.getPaymentStatus())
                .amount(payment.getAmount())
                .build();
    }
}
