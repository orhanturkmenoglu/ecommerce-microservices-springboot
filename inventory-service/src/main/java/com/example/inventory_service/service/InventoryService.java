package com.example.inventory_service.service;

import com.example.inventory_service.dto.InventoryRequestDto;
import com.example.inventory_service.dto.InventoryResponseDto;
import com.example.inventory_service.dto.InventoryUpdateRequestDto;
import com.example.inventory_service.exception.ProductNotFoundException;
import com.example.inventory_service.mapper.InventoryMapper;
import com.example.inventory_service.model.Inventory;
import com.example.inventory_service.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        Inventory inventory = inventoryMapper.mapToInventory(inventoryRequestDto);

        log.info("InventoryService::addInventory inventory :{}", inventory);
        Inventory savedInventory = inventoryRepository.save(inventory);

        log.info("InventoryService::addInventory savedInventory :{}", savedInventory);

        log.info("InventoryService::addInventory finished");

        return inventoryMapper.mapToInventoryResponseDto(savedInventory);
    }

    @Cacheable(value = "inventories",key = "'all'")
    public List<InventoryResponseDto> getAllInventories() {
        log.info("InventoryService::getAllInventories started");

        List<Inventory> inventoryList = inventoryRepository.findAll();

        log.info("InventoryService::getAllInventories finished");
        return inventoryMapper.mapToInventoryResponseDtoList(inventoryList);
    }

    @Cacheable(value = "inventories",key = "#productId")
    public InventoryResponseDto getInventoryByProductId(String productId) {
        log.info("InventoryService::getInventoryByProductId started");

        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ProductNotFoundException("Inventory not found for product ID: " + productId));


        log.info("InventoryService::getInventoryByProductId finished");
        return inventoryMapper.mapToInventoryResponseDto(inventory);
    }

    @Cacheable(value = "inventories",key = "#id")
    public InventoryResponseDto getInventoryById(String id) {
        log.info("InventoryService::getInventoryById started");

        Inventory inventory = getInventory(id);

        log.info("InventoryService::getInventoryById inventory :{}", inventory);


        log.info("InventoryService::getInventoryById finished");
        return inventoryMapper.mapToInventoryResponseDto(inventory);
    }


    @Transactional
    @CacheEvict(value = "inventories",key = "#inventoryId",allEntries = true)
    public InventoryResponseDto updateInventory(String inventoryId, InventoryUpdateRequestDto inventoryUpdateRequestDto) {
        log.info("InventoryService::updateInventory started");

        Inventory inventory = getInventory(inventoryId);

        log.info("InventoryService::updateInventory inventory :{} ", inventory);
        inventory.setProductId(inventoryUpdateRequestDto.getProductId());
        inventory.setStockQuantity(inventoryUpdateRequestDto.getNewQuantity());

        Inventory updatedInventory = inventoryRepository.save(inventory);

        log.info("InventoryService::updateInventory updatedInventory : {}", updatedInventory);


        log.info("InventoryService::updateInventory finished");
        return inventoryMapper.mapToInventoryResponseDto(updatedInventory);
    }

    @CacheEvict(value = "inventories",key = "#productId",allEntries = true)
    public void deleteInventory(String productId) {
        log.info("InventoryService::deleteInventory started");

        InventoryResponseDto inventoryByProductId = getInventoryByProductId(productId);
        Inventory mapToInventory = inventoryMapper.mapToInventory(inventoryByProductId);

        inventoryRepository.delete(mapToInventory);

        log.info("InventoryService::deleteInventory finished");
    }

    private Inventory getInventory(String id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new NullPointerException("Inventory not found for inventory ID: " + id));
    }
}
