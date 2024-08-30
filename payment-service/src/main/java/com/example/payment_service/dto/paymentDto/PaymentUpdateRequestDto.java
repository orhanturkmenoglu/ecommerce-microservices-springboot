package com.example.payment_service.dto.paymentDto;

import com.example.payment_service.enums.PaymentType;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Data transfer object for updating payment information")
public class PaymentUpdateRequestDto implements Serializable {

    @NotEmpty(message = "Payment customerId cannot be empty")
    @Schema(description = "Unique identifier of the customer making the payment", example = "cust123")
    private String customerId;

    @NotEmpty(message = "Payment orderId cannot be empty")
    @Schema(description = "Unique identifier of the order associated with the payment", example = "order456")
    private String orderId;

    @NotEmpty(message = "Payment amount cannot be empty")
    @Schema(description = "Amount of the payment", example = "150.75")
    private Double amount;

    @NotNull(message = "Payment type  cannot be empty")
    @Schema(description = "Type of payment being made", example = "CREDIT_CARD"    )
    private PaymentType paymentType;
}
