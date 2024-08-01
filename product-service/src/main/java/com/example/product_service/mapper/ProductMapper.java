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
                .inventoryId(productRequestDto.getInventoryRequestDto().getId())
                .name(productRequestDto.getName())
                .description(productRequestDto.getDescription())
                .category(productRequestDto.getCategory())
                .price(productRequestDto.getPrice())
                .inventory(mapToInventory(productRequestDto.getInventoryRequestDto()))
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

    public ProductRequestDto mapToProductRequestDto(Product product) {
        return ProductRequestDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .category(product.getCategory())
                .price(product.getPrice())
                .inventoryRequestDto(mapToInventoryRequestDto(product.getInventory()))
                .build();
    }

    public List<ProductResponseDto> mapToProductResponseDtoList(List<Product> productList) {
        return productList.stream()
                .map(this::mapToProductResponseDto)
                .toList();
    }

    public Inventory mapToInventory(InventoryResponseDto inventoryResponseDto) {
        return Inventory.builder()
                .id(inventoryResponseDto.getId())
                .productId(inventoryResponseDto.getProductId())
                .stockQuantity(inventoryResponseDto.getStockQuantity())
                .build();
    }

    public InventoryResponseDto mapToInventoryResponseDto(Inventory inventory) {
        return InventoryResponseDto.builder()
                .id(inventory.getId())
                .productId(inventory.getProductId())
                .stockQuantity(inventory.getStockQuantity())
                .build();
    }

    public Inventory mapToInventory(InventoryRequestDto inventoryRequestDto) {
        return Inventory.builder()
                .id(inventoryRequestDto.getId())
                .productId(inventoryRequestDto.getProductId())
                .stockQuantity(inventoryRequestDto.getStockQuantity())
                .build();
    }

    public InventoryRequestDto mapToInventoryRequestDto(Inventory inventory) {
        return InventoryRequestDto.builder()
                .id(inventory.getId())
                .productId(inventory.getProductId())
                .stockQuantity(inventory.getStockQuantity())
                .build();
    }


}
