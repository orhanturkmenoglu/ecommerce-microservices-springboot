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
@Schema(description = "Data transfer object for updating inventory information")
public class InventoryUpdateRequestDto implements Serializable {
    @Schema(description = "Unique identifier for the inventory item", example = "inv123")
    @NotEmpty(message = "Inventory id cannot be empty")
    private String inventoryId;

    @Schema(description = "Unique identifier for the product associated with the inventory", example = "prod456")
    @NotEmpty(message = "Inventory product Id cannot be empty")
    private String productId;

    @Schema(description = "New quantity of stock available in the inventory", example = "20")
    @NotNull(message = "Inventory new quantity cannot be null")
    private int newQuantity;

    @Schema(description = "Timestamp of the last update to the inventory record", example = "2024-08-30T12:00:00")
    private LocalDateTime lastUpdated;
}
