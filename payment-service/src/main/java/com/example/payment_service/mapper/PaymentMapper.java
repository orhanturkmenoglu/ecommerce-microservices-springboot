package com.example.payment_service.mapper;

import com.example.payment_service.dto.paymentDto.PaymentRequestDto;
import com.example.payment_service.dto.paymentDto.PaymentResponseDto;
import com.example.payment_service.enums.PaymentStatus;
import com.example.payment_service.model.Payment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class PaymentMapper {

    public Payment mapToPayment(PaymentRequestDto paymentRequestDto) {
        return Payment.builder()
                .customerId(paymentRequestDto.getCustomerId())
                .orderId(paymentRequestDto.getOrderId())
                .cargoId(paymentRequestDto.getCargoId())
                .paymentStatus(PaymentStatus.PENDING) // Başlangıçta ödeme durumu PENDING
                .paymentType(paymentRequestDto.getPaymentType())
                .paymentDate(LocalDateTime.now())
                .amount(0.0) // Burada amount değerini atıyoruz
                .quantity(0) // Burada quantity değerini atıyoruz
                .build();
    }

    public PaymentResponseDto mapToPaymentResponseDto(Payment payment) {
        return PaymentResponseDto.builder()
                .id(payment.getId())
                .customerId(payment.getCustomerId())
                .orderId(payment.getOrderId())
                .cargoId(payment.getCargoId())
                .paymentStatus(payment.getPaymentStatus())
                .quantity(payment.getQuantity())
                .amount(payment.getAmount())
                .paymentType(payment.getPaymentType())
                .paymentDate(payment.getPaymentDate())
                .build();
    }

    public List<PaymentResponseDto> maptoPaymentResponseDtoList(List<Payment> payments) {
        return payments.stream()
                .map(this::mapToPaymentResponseDto)
                .toList();
    }

}
