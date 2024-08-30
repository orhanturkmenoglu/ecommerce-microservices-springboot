package com.example.spring.boot.dto.cargoDto;


import com.example.spring.boot.enums.CargoStatus;
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
@Schema(description = "Data transfer object for cargo request")
public class CargoRequestDto implements Serializable {

    @Schema(description = "Unique identifier for the order associated with the cargo", example = "order789")
    private String orderId;

    @Schema(description = "Unique identifier for the customer associated with the cargo", example = "cust123")
    private String customerId;

    @Schema(description = "Tracking number for the cargo", example = "TRACK123456789")
    private String trackingNumber;

    @Schema(description = "Current status of the cargo", example = "Shipped")
    private CargoStatus status;

    @Schema(description = "Last update timestamp for the cargo status", example = "2024-08-30T14:00:00")
    private LocalDateTime lastUpdated;
}