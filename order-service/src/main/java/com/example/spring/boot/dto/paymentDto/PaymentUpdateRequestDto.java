package com.example.spring.boot.dto.paymentDto;

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

    @NotEmpty(message = "Payment order id cannot be empty")
    private String orderId;

    @NotNull(message = "Payment amount cannot be null")
    private Double amount;
}
