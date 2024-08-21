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
public class InventoryRequestDto implements Serializable {

    private String id;

    @NotEmpty(message = "Inventory productId cannot be empty")
    private String productId;

    @NotNull(message = "Inventory stock quantity cannot be null")
    private int stockQuantity;

    private LocalDateTime lastUpdated;
}
