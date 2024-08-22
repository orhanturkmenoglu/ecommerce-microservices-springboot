package com.example.product_service.mapper;


import com.example.product_service.dto.inventoryDto.InventoryRequestDto;
import com.example.product_service.dto.inventoryDto.InventoryResponseDto;
import com.example.product_service.dto.inventoryDto.InventoryUpdateRequestDto;
import com.example.product_service.dto.productDto.ProductRequestDto;
import com.example.product_service.dto.productDto.ProductResponseDto;
import com.example.product_service.model.Inventory;
import com.example.product_service.model.Product;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
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
                .createdDate(LocalDateTime.now())
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
                .createdDate(LocalDateTime.now())
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


    public InventoryUpdateRequestDto mapToInventoryUpdateRequestDto(Inventory inventory) {
        return InventoryUpdateRequestDto.builder()
                .inventoryId(inventory.getId())
                .productId(inventory.getProductId())
                .newQuantity(inventory.getStockQuantity())
                .build();
    }


}
