package com.example.inventory_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Data transfer object representing the details of an inventory item")
public class InventoryResponseDto implements Serializable {

    @Schema(description = "Unique identifier for the inventory", example = "inv123")
    private String id;

    @Schema(description = "Unique identifier for the product associated with the inventory", example = "prod456")
    private String productId;

    @Schema(description = "Quantity of stock available in the inventory", example = "100")
    private int stockQuantity;

    @Schema(description = "Timestamp of the last update to the inventory record", example = "2024-08-30T12:00:00")
    private LocalDateTime lastUpdated;
}
