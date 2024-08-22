package com.example.inventory_service.mapper;

import com.example.inventory_service.dto.InventoryRequestDto;
import com.example.inventory_service.dto.InventoryResponseDto;
import com.example.inventory_service.dto.InventoryUpdateRequestDto;
import com.example.inventory_service.model.Inventory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class InventoryMapper {


    public Inventory mapToInventory(InventoryRequestDto inventoryRequestDto) {
        return Inventory.builder()
                .id(inventoryRequestDto.getId())
                .productId(inventoryRequestDto.getProductId())
                .stockQuantity(inventoryRequestDto.getStockQuantity())
                .lastUpdated(LocalDateTime.now())
                .build();
    }


    public InventoryRequestDto mapToInventoryRequestDto(Inventory inventory) {
        return InventoryRequestDto.builder()
                .id(inventory.getId())
                .productId(inventory.getProductId())
                .stockQuantity(inventory.getStockQuantity())
                .build();
    }


    public Inventory mapToInventory(InventoryResponseDto inventoryResponseDto) {
        return Inventory.builder()
                .id(inventoryResponseDto.getId())
                .productId(inventoryResponseDto.getProductId())
                .stockQuantity(inventoryResponseDto.getStockQuantity())
                .lastUpdated(LocalDateTime.now())
                .build();
    }


    public InventoryResponseDto mapToInventoryResponseDto(Inventory inventory) {
        return InventoryResponseDto.builder()
                .id(inventory.getId())
                .productId(inventory.getProductId())
                .stockQuantity(inventory.getStockQuantity())
                .lastUpdated(LocalDateTime.now())
                .build();
    }

    public List<InventoryResponseDto> mapToInventoryResponseDtoList(List<Inventory> inventoryList) {
        return inventoryList.stream()
                .map(this::mapToInventoryResponseDto)
                .collect(Collectors.toList());
    }

    public InventoryUpdateRequestDto mapToInventoryUpdateRequestDto(Inventory inventory) {
        return InventoryUpdateRequestDto.builder()
                .newQuantity(inventory.getStockQuantity())
                .lastUpdated(LocalDateTime.now())
                .build();
    }
}
