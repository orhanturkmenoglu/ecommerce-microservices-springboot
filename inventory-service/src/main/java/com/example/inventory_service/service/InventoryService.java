package com.example.inventory_service.service;

import com.example.inventory_service.dto.InventoryRequestDto;
import com.example.inventory_service.dto.InventoryResponseDto;
import com.example.inventory_service.exception.ProductNotFoundException;
import com.example.inventory_service.mapper.InventoryMapper;
import com.example.inventory_service.model.Inventory;
import com.example.inventory_service.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;

    @Transactional
    public InventoryResponseDto addInventory(InventoryRequestDto inventoryRequestDto) {

        log.info("InventoryService::addInventory started");

        inventoryRequestDto.setLastUpdated(LocalDateTime.now());

        Inventory inventory = inventoryMapper.mapToInventory(inventoryRequestDto);
        Inventory savedInventory = inventoryRepository.save(inventory);

        log.info("InventoryService::addInventory finished");
        return inventoryMapper.mapToInventoryResponseDto(savedInventory);
    }

    public List<InventoryResponseDto> getAllInventories() {
        log.info("InventoryService::getAllInventories started");

        List<Inventory> inventoryList = inventoryRepository.findAll();

        log.info("InventoryService::getAllInventories finished");
        return inventoryMapper.mapToInventoryResponseDtoList(inventoryList);
    }


    public InventoryResponseDto getInventoryByProductId(String productId) {
        log.info("InventoryService::getInventoryByProductId started");

        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ProductNotFoundException("Inventory not found for product ID: " + productId));


        log.info("InventoryService::getInventoryByProductId finished");
        return inventoryMapper.mapToInventoryResponseDto(inventory);
    }


    public InventoryResponseDto getInventoryById(String id) {
        log.info("InventoryService::getInventoryById started");

        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new NullPointerException("Inventory not found for inventory ID: " + id));

        log.info("InventoryService::getInventoryById finished");
        return inventoryMapper.mapToInventoryResponseDto(inventory);
    }


    public InventoryResponseDto updateInventory(String inventoryId, InventoryRequestDto inventoryRequestDto) {
        log.info("InventoryService::updateInventory started");

        InventoryResponseDto inventoryById = getInventoryById(inventoryId);

        inventoryById.setProductId(inventoryRequestDto.getProductId());
        inventoryById.setStockQuantity(inventoryRequestDto.getStockQuantity());
        inventoryById.setLastUpdated(LocalDateTime.now());

        Inventory mapToInventory = inventoryMapper.mapToInventory(inventoryById);
        Inventory updatedInventory = inventoryRepository.save(mapToInventory);

        log.info("InventoryService::updateInventory finished");
        return inventoryMapper.mapToInventoryResponseDto(updatedInventory);
    }

    public void deleteInventory(String productId) {
        log.info("InventoryService::deleteInventory started");

        InventoryResponseDto inventoryByProductId = getInventoryByProductId(productId);
        Inventory mapToInventory = inventoryMapper.mapToInventory(inventoryByProductId);

        inventoryRepository.delete(mapToInventory);

        log.info("InventoryService::deleteInventory finished");
    }
}
