package com.example.product_service.dto.inventoryDto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Data transfer object for updating inventory")
public class InventoryUpdateRequestDto implements Serializable {

    @NotEmpty(message = "Inventory  Id cannot be empty")
    @Schema(description = "Unique identifier of the inventory", example = "inv123")
    private String inventoryId;

    @NotEmpty(message = "Inventory product Id cannot be empty")
    @Schema(description = "Unique identifier of the product associated with the inventory", example = "prod456")
    private String productId;

    @NotNull(message = "Inventory stock quantity cannot be null")
    @Min(value = 0,message = "Inventory stock quantity must be 0")
    @Schema(description = "New stock quantity for the inventory", example = "20")
    private int newQuantity;
}
