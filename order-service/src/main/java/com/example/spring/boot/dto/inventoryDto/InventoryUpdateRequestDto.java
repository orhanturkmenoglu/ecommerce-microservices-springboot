package com.example.spring.boot.dto.inventoryDto;

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
@Schema(description = "Data transfer object for inventory update request")
public class InventoryUpdateRequestDto implements Serializable {

    @Schema(description = "Unique identifier for the inventory", example = "inventory123")
    @NotEmpty(message = "Inventory id cannot be empty")
    private String inventoryId;

    @Schema(description = "New quantity for the inventory", example = "100")
    @NotNull(message = "Inventory new quantity cannot be null")
    private int newQuantity;

    @Schema(description = "Last update timestamp for the inventory", example = "2024-08-30T14:00:00")
    private LocalDateTime lastUpdated;
}
