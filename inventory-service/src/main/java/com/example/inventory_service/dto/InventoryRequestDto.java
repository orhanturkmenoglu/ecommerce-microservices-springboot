package com.example.inventory_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
@Schema(description = "Data transfer object for creating or updating inventory information")
public class InventoryRequestDto implements Serializable {

    @Schema(description = "Unique identifier for the inventory", example = "inv123")
    private String id;

    @Schema(description = "Unique identifier for the product associated with the inventory", example = "prod456")
    @NotEmpty(message = "Inventory productId cannot be empty")
    private String productId;

    @Schema(description = "Quantity of stock available in the inventory", example = "10")
    @NotNull(message = "Inventory stock quantity cannot be null")
    private int stockQuantity;
}
