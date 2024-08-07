package com.example.product_service.dto.inventoryDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryResponseDto implements Serializable {

    private String id;
    private String productId;
    private int stockQuantity;
}
