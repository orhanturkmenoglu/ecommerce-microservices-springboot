package com.example.product_service.dto;

import com.example.product_service.model.Inventory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponseDto implements Serializable {
    private String id;
    private String inventoryId;
    private String name;
    private String description;
    private String category;
    private double price;
    private InventoryResponseDto inventoryResponseDto;
}
