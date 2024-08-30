package com.example.spring.boot.dto.paymentDto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Data transfer object for updating payment information")
public class PaymentUpdateRequestDto {

    @Schema(description = "Unique identifier for the customer associated with the payment", example = "customer123")
    @NotEmpty(message = "Payment customerId cannot be empty")
    private String customerId;

    @Schema(description = "Unique identifier for the order related to the payment", example = "order456")
    @NotEmpty(message = "Payment orderId cannot be empty")
    private String orderId;

    @Schema(description = "Amount of the payment", example = "199.99")
    @NotNull(message = "Payment amount cannot be null")
    private Double amount;
}
