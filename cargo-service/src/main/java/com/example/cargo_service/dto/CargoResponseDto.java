package com.example.cargo_service.dto;


import com.example.cargo_service.enums.CargoStatus;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "CargoResponseDto", description = "DTO for representing cargo details in the response")
public class CargoResponseDto implements Serializable {

    @Schema(description = "Unique identifier for the cargo", example = "cargo123")
    private String id;

    @Schema(description = "ID of the order associated with the cargo", example = "order123")
    private String orderId;

    @Schema(description = "ID of the customer associated with the cargo", example = "customer123")
    private String customerId;

    @Schema(description = "Tracking number of the cargo", example = "TRK123456789")
    private String trackingNumber;

    @Schema(description = "Status of the cargo", example = "Delivered")
    private CargoStatus status;

    @Schema(description = "Last updated timestamp of the cargo", example = "2024-08-30T10:15:30")
    private LocalDateTime lastUpdated;
}