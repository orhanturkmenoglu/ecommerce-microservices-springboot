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
@Schema(description = "Data transfer object for payment request")
public class PaymentRequestDto implements Serializable {

    @NotEmpty(message = "Payment customer  id cannot be empty")
    @Schema(description = "Unique identifier of the customer making the payment", example = "cust123")
    private String customerId;


    @NotEmpty(message = "Order id cannot be empty")
    @Schema(description = "Unique identifier of the order being paid for", example = "order456")
    private String orderId;

    @NotEmpty(message = "Cargo id cannot be empty")
    @Schema(description = "Unique identifier of the cargo associated with the payment", example = "cargo789")
    private String cargoId;


    @NotNull(message = "Payment type  cannot be empty")
    @Schema(description = "Type of payment being made", example = "CREDIT_CARD")
    private PaymentType paymentType;
}
