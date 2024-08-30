package com.example.product_service.dto.inventoryDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
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
@Schema(description = "Data transfer object for inventory requests")
public class InventoryRequestDto implements Serializable {

    @Schema(description = "Unique identifier of the inventory")
    private String id;

    @Schema(description = "Unique identifier of the associated product")
    private String productId;

    @NotNull(message = "Inventory stock quantity cannot be null")
    @Min(value = 0,message = "Inventory stock quantity must be 0")
    @Schema(description = "Stock quantity of the inventory", example = "10")
    private int stockQuantity;
}
