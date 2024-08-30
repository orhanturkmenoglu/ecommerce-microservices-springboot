package com.example.product_service.dto.productDto;

import com.example.product_service.dto.inventoryDto.InventoryUpdateRequestDto;
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
@Schema(description = "Data transfer object for updating product information")
public class ProductUpdateRequestDto implements Serializable {

    @Schema(description = "Unique identifier of the product to be updated", example = "prod789")
    private String id;

    @NotEmpty(message = "Product name cannot be empty ")
    @Schema(description = "Name of the product", example = "Smartphone")
    private String name;

    @NotEmpty(message = "Product description cannot be empty ")
    @Schema(description = "Description of the product", example = "Latest model with advanced features.")
    private String description;

    @NotEmpty(message = "Product category cannot be empty ")
    @Schema(description = "Category of the product", example = "ELECTRONICS")
    private Category category;

    @NotNull(message = "Product price cannot be null")
    @Min(message = "Product price must be greater than zero", value = 0)
    @Schema(description = "Price of the product", example = "299.99")
    private double price;


    private InventoryUpdateRequestDto inventoryUpdateRequestDto;
}
