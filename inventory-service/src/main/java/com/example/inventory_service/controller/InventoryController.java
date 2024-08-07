package com.example.inventory_service.controller;

import com.example.inventory_service.dto.InventoryRequestDto;
import com.example.inventory_service.dto.InventoryResponseDto;
import com.example.inventory_service.dto.InventoryUpdateRequestDto;
import com.example.inventory_service.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventories")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;
    

    @PostMapping("/create")
    public ResponseEntity<InventoryResponseDto> addInventory(@Valid @RequestBody InventoryRequestDto inventoryRequestDto) {
        InventoryResponseDto inventoryResponseDto = inventoryService.addInventory(inventoryRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryResponseDto);
    }

    @GetMapping("/all")
    public ResponseEntity<List<InventoryResponseDto>> getAllInventories() {
        List<InventoryResponseDto> responseDtoList = inventoryService.getAllInventories();
        return ResponseEntity.ok(responseDtoList);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<InventoryResponseDto> getInventoryByProductId(@PathVariable String productId) {
        InventoryResponseDto inventoryResponseDto = inventoryService.getInventoryByProductId(productId);
        return ResponseEntity.ok(inventoryResponseDto);
    }

    @GetMapping("/getInventoryId/{id}")
    public ResponseEntity<InventoryResponseDto> getInventoryById(@PathVariable String id) {
        InventoryResponseDto inventoryResponseDto = inventoryService.getInventoryById(id);
        return ResponseEntity.ok(inventoryResponseDto);
    }

    @PutMapping("/{inventoryId}")
    public ResponseEntity<InventoryResponseDto> updateInventory(@PathVariable String inventoryId, @RequestBody InventoryUpdateRequestDto inventoryRequestDto) {
        InventoryResponseDto inventoryResponseDto = inventoryService.updateInventory(inventoryId, inventoryRequestDto);
        return ResponseEntity.ok(inventoryResponseDto);
    }

    @DeleteMapping("/{productId}")
    public void deleteInventory(@PathVariable String productId) {
        inventoryService.deleteInventory(productId);
    }
}
