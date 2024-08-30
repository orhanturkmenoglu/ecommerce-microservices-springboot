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
                .paymentStatus(PaymentStatus.COMPLETED)
                .paymentType(paymentRequestDto.getPaymentType())
                .paymentDate(LocalDateTime.now())
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
                .paymentDate(LocalDateTime.now())
                .build();
    }

    public List<PaymentResponseDto> maptoPaymentResponseDtoList(List<Payment> payments) {
        return payments.stream()
                .map(this::mapToPaymentResponseDto)
                .toList();
    }

}
