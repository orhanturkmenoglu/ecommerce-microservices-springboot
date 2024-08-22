package com.example.product_service.dto.productDto;

import com.example.product_service.dto.inventoryDto.InventoryResponseDto;
import com.example.product_service.enums.Category;
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
public class ProductResponseDto implements Serializable {

    private String id;
    private String inventoryId;
    private String name;
    private String description;
    private Category category;
    private double price;
    private LocalDateTime createdDate;
    private InventoryResponseDto inventoryResponseDto;
}
