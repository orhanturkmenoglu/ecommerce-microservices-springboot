package com.example.inventory_service.dto;

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
public class InventoryUpdateRequestDto implements Serializable {

    @NotEmpty(message = "Inventory id cannot be empty")
    private String inventoryId;

    @NotNull(message = "Inventory new quantity cannot be null")
    private int newQuantity;

    private LocalDateTime lastUpdated;
}
