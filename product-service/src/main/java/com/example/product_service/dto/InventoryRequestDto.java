package com.example.product_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryRequestDto {

    private String id;
    private String productId;
    @NotNull(message = "Inventory stock quantity cannot be null")
    @Min(value = 0,message = "Inventory stock quantity must be 0")
    private int stockQuantity;
}
