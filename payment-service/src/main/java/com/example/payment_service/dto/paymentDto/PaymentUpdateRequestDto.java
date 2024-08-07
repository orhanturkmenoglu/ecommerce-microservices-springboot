package com.example.payment_service.dto.paymentDto;

import com.example.payment_service.enums.PaymentType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentUpdateRequestDto {

    @NotEmpty(message = "Payment orderId cannot be empty")
    private String orderId;

    @NotEmpty(message = "Payment amount cannot be empty")
    private Double amount;

    @NotNull(message = "Payment type  cannot be empty")
    private PaymentType paymentType;
}
