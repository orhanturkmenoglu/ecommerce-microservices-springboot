package com.example.inventory_service.mapper;

import com.example.inventory_service.dto.InventoryRequestDto;
import com.example.inventory_service.dto.InventoryResponseDto;
import com.example.inventory_service.model.Inventory;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface InventoryMapper {

    InventoryMapper INSTANCE = Mappers.getMapper(InventoryMapper.class);

    Inventory mapToInventory(InventoryRequestDto inventoryRequestDto);

    Inventory mapToInventory(InventoryResponseDto inventoryResponseDto);

    InventoryResponseDto mapToInventoryResponseDto(Inventory inventory);

    List<InventoryResponseDto> mapToInventoryResponseDtoList(List<Inventory> inventoryList);
}
