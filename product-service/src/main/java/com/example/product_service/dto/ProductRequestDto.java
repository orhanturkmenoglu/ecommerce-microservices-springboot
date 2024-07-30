package com.example.product_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
public class ProductRequestDto implements Serializable {

    private String id;

    @NotEmpty(message = "Product name cannot be empty ")
    @Min(value = 2,message = "Product name must be at least 2 characters")
    private String name;

    @NotEmpty(message = "Product description cannot be empty ")
    private String description;

    @NotEmpty(message = "Product category cannot be empty ")
    private String category;

    @NotNull(message = "Product price cannot be null")
    @Min(message = "Product price must be greater than zero",value = 0)
    private double price;


    private InventoryRequestDto inventoryRequestDto;
}
