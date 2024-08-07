package com.example.payment_service.dto.paymentDto;

import com.example.payment_service.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponseDto implements Serializable {
    private String id;
    private String orderId;
    private Double amount;
    private PaymentStatus paymentStatus;
}
