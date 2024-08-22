package com.example.product_service.external;


import com.example.product_service.dto.inventoryDto.InventoryRequestDto;
import com.example.product_service.dto.inventoryDto.InventoryResponseDto;
import com.example.product_service.dto.inventoryDto.InventoryUpdateRequestDto;
import com.example.product_service.model.Inventory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "INVENTORY-SERVICE")
public interface InventoryClientService {


    @PostMapping("/api/v1/inventories/create")
    InventoryResponseDto addInventory(@RequestBody InventoryRequestDto inventoryRequestDto);

    @GetMapping("/api/v1/inventories/{productId}")
    Inventory getInventoryByProductId(@PathVariable("productId") String productId);

    @PutMapping("/api/v1/inventories/{inventoryId}")
    InventoryResponseDto updateInventory(@PathVariable("inventoryId") String inventoryId, @RequestBody InventoryUpdateRequestDto inventoryUpdateRequestDto);

    @DeleteMapping("/api/v1/inventories/{productId}")
    void deleteInventory(@PathVariable("productId") String productId);

    @GetMapping("/api/v1/inventories/getInventoryId/{id}")
    Inventory getInventoryById(@PathVariable String id);

}
