package com.example.product_service.dto.productDto;

import com.example.product_service.dto.inventoryDto.InventoryRequestDto;
import com.example.product_service.enums.Category;
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
@Schema(description = "Product entity representing the product details")
public class ProductRequestDto implements Serializable {

    @Schema(description = "Unique identifier of the product")
    private String id;

    @Schema(description = "Name of the product", example = "Laptop")
    @NotEmpty(message = "Product name cannot be empty ")
    private String name;

    @Schema(description = "Description of the product", example = "High-performance laptop for gaming and work")
    @NotEmpty(message = "Product description cannot be empty ")
    private String description;

    @NotEmpty(message = "Product category cannot be empty ")
    @Schema(description = "Category of the product", example = "ELEKTRONİK")
    private Category category;

    @NotNull(message = "Product price cannot be null")
    @Min(message = "Product price must be greater than zero", value = 0)
    @Schema(description = "Price of the product", example = "999.99")
    private double price;


    private InventoryRequestDto inventoryRequestDto;
}
