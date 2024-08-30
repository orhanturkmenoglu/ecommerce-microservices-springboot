package com.example.cargo_service.dto;


import com.example.cargo_service.enums.CargoStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(name = "CargoUpdateRequestDto", description = "DTO for updating cargo details")
public class CargoUpdateRequestDto implements Serializable {

    @Schema(description = "Unique identifier for the cargo", example = "cargo123")
    private String id;

    @Schema(description = "ID of the order associated with the cargo", example = "order123")
    @NotEmpty(message = "Order ID cannot be empty")
    private String orderId;

    @Schema(description = "ID of the customer associated with the cargo", example = "customer123")
    @NotEmpty(message = "Customer ID cannot be empty")
    private String customerId;

    @Schema(description = "Tracking number of the cargo", example = "TRK123456789")
    @NotEmpty(message = "Tracking number cannot be empty")
    private String trackingNumber;

    @Schema(description = "Status of the cargo", example = "In Transit")
    @NotEmpty(message = "Status cannot be empty")
    private CargoStatus status;

    @Schema(description = "Last updated timestamp of the cargo", example = "2024-08-30T10:15:30")
    private LocalDateTime lastUpdated;
}