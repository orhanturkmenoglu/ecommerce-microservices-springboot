package com.example.product_service.mapper;


import com.example.product_service.dto.InventoryRequestDto;
import com.example.product_service.dto.InventoryResponseDto;
import com.example.product_service.dto.ProductRequestDto;
import com.example.product_service.dto.ProductResponseDto;
import com.example.product_service.model.Inventory;
import com.example.product_service.model.Product;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductMapper {


    public Product mapToProduct(ProductRequestDto productRequestDto) {
        return Product.builder()
                .name(productRequestDto.getName())
                .description(productRequestDto.getDescription())
                .category(productRequestDto.getCategory())
                .price(productRequestDto.getPrice())
                .inventory(mapToInventoryRequestDto(productRequestDto.getInventoryRequestDto()))
                .build();
    }

    public ProductResponseDto mapToProductResponseDto(Product product) {
        return ProductResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .category(product.getCategory())
                .price(product.getPrice())
                .inventoryId(product.getInventoryId())
                .inventoryResponseDto(mapToInventoryResponseDto(product.getInventory()))
                .build();
    }

    public List<ProductResponseDto> mapToProductResponseDtoList(List<Product> productList) {
        return productList.stream()
                .map(this::mapToProductResponseDto)
                .toList();
    }

    public Inventory mapToInventory(InventoryResponseDto inventoryResponseDto) {
        return Inventory.builder()
                .stockQuantity(inventoryResponseDto.getStockQuantity())
                .build();
    }

    public InventoryResponseDto mapToInventoryResponseDto(Inventory inventory) {
        return InventoryResponseDto.builder()
                .stockQuantity(inventory.getStockQuantity())
                .build();
    }

    public Inventory mapToInventoryRequestDto(InventoryRequestDto inventoryRequestDto) {
        return Inventory.builder()
                .id(inventoryRequestDto.getId())
                .stockQuantity(inventoryRequestDto.getStockQuantity())
                .build();
    }
}
