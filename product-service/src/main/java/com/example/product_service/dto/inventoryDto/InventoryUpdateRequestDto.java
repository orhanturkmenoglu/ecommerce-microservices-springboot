package com.example.product_service.dto.inventoryDto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryUpdateRequestDto implements Serializable {

    @NotEmpty(message = "Inventory  Id cannot be empty")
    private String inventoryId;

    @NotEmpty(message = "Inventory product Id cannot be empty")
    private String productId;

    @NotNull(message = "Inventory stock quantity cannot be null")
    @Min(value = 0,message = "Inventory stock quantity must be 0")
    private int newQuantity;
}
