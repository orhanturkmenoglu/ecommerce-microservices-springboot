package com.example.product_service.dto.inventoryDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Data transfer object for inventory response")
public class InventoryResponseDto implements Serializable {

    @Schema(description = "Unique identifier of the inventory", example = "123")
    private String id;

    @Schema(description = "Unique identifier of the associated product", example = "456")
    private String productId;

    @Schema(description = "Current stock quantity of the inventory", example = "100")
    private int stockQuantity;
}
