package com.example.product_service.dto.productDto;

import com.example.product_service.dto.inventoryDto.InventoryResponseDto;
import com.example.product_service.enums.Category;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Data transfer object for product response")
public class ProductResponseDto implements Serializable {

    @Schema(description = "Unique identifier of the product", example = "prod789")
    private String id;

    @Schema(description = "Unique identifier of the associated inventory", example = "inv123")
    private String inventoryId;

    @Schema(description = "Name of the product", example = "Laptop")
    private String name;

    @Schema(description = "Description of the product", example = "A high-performance laptop with 16GB RAM and 512GB SSD.")
    private String description;

    @Schema(description = "Category of the product", example = "ELECTRONICS")
    private Category category;

    @Schema(description = "Price of the product", example = "999.99")
    private double price;

    @Schema(description = "Date and time when the product was created", example = "2024-08-29T10:15:30")
    private LocalDateTime createdDate;


    private InventoryResponseDto inventoryResponseDto;
}
