package com.example.payment_service.dto.paymentDto;

import com.example.payment_service.enums.PaymentType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequestDto implements Serializable {

    @NotEmpty(message = "Payment customer  id cannot be empty")
    private String customerId;

    @NotEmpty(message = "Payment order id cannot be empty")
    private String orderId;

    @NotNull(message = "Payment type  cannot be empty")
    private PaymentType paymentType;
}
