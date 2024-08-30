package com.example.payment_service.dto.paymentDto;

import com.example.payment_service.enums.PaymentStatus;
import com.example.payment_service.enums.PaymentType;
import com.example.payment_service.model.Customer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Embedded;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Data transfer object for payment response")
public class PaymentResponseDto implements Serializable {

    @Schema(description = "Unique identifier of the payment", example = "pay123")
    private String id;

    @Schema(description = "Unique identifier of the customer who made the payment", example = "cust456")
    private String customerId;

    @Schema(description = "Unique identifier of the order associated with the payment", example = "order789")
    private String orderId;

    @Schema(description = "Unique identifier of the cargo associated with the payment", example = "cargo012")
    private String cargoId;

    @Schema(description = "Quantity of items paid for", example = "5")
    private int quantity;

    @Schema(description = "Amount of the payment", example = "150.75")
    private Double amount;

    @Schema(description = "Status of the payment", example = "SUCCESS")
    private PaymentStatus paymentStatus;

    @Schema(description = "Type of the payment", example = "CREDIT_CARD")
    private PaymentType paymentType;

    @Schema(description = "Date and time when the payment was made", example = "2024-08-30T15:00:00")
    private LocalDateTime paymentDate;

    @Embedded
    @Schema(description = "Details of the customer who made the payment")
    private Customer customer ;
}
