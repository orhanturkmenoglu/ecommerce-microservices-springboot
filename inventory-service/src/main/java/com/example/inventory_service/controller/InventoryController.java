package com.example.inventory_service.controller;

import com.example.inventory_service.dto.InventoryRequestDto;
import com.example.inventory_service.dto.InventoryResponseDto;
import com.example.inventory_service.dto.InventoryUpdateRequestDto;
import com.example.inventory_service.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Add new inventory", description = "Creates a new inventory entry.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Inventory successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid input provided")
    })
    public ResponseEntity<InventoryResponseDto> addInventory(
            @Parameter(description = "Inventory request details", required = true)
            @Valid @RequestBody InventoryRequestDto inventoryRequestDto) {

        InventoryResponseDto inventoryResponseDto = inventoryService.addInventory(inventoryRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryResponseDto);
    }

    @GetMapping("/all")
    @Operation(summary = "Get all inventories", description = "Retrieves all inventory entries.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved inventories"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<InventoryResponseDto>> getAllInventories() {
        List<InventoryResponseDto> responseDtoList = inventoryService.getAllInventories();
        return ResponseEntity.ok(responseDtoList);
    }

    @GetMapping("/{productId}")
    @Operation(summary = "Get inventory by product ID", description = "Retrieves inventory details for a specific product ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved inventory"),
            @ApiResponse(responseCode = "404", description = "Inventory not found for the given product ID")
    })
    public ResponseEntity<InventoryResponseDto> getInventoryByProductId(
            @Parameter(description = "Product ID for which inventory details are to be retrieved", required = true)
            @PathVariable String productId) {

        InventoryResponseDto inventoryResponseDto = inventoryService.getInventoryByProductId(productId);
        return ResponseEntity.ok(inventoryResponseDto);
    }

    @GetMapping("/getInventoryId/{id}")
    @Operation(summary = "Get inventory by ID", description = "Retrieves inventory details for a specific inventory ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved inventory"),
            @ApiResponse(responseCode = "404", description = "Inventory not found for the given ID")
    })
    public ResponseEntity<InventoryResponseDto> getInventoryById(
            @Parameter(description = "Inventory ID for which details are to be retrieved", required = true)
            @PathVariable String id) {

        InventoryResponseDto inventoryResponseDto = inventoryService.getInventoryById(id);
        return ResponseEntity.ok(inventoryResponseDto);
    }

    @PutMapping("/{inventoryId}")
    @Operation(summary = "Update inventory", description = "Updates an existing inventory entry.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventory successfully updated"),
            @ApiResponse(responseCode = "400", description = "Invalid input provided"),
            @ApiResponse(responseCode = "404", description = "Inventory not found for the given ID")
    })
    public ResponseEntity<InventoryResponseDto> updateInventory(
            @Parameter(description = "Inventory ID to be updated", required = true)
            @PathVariable String inventoryId,
            @Parameter(description = "Inventory update request details", required = true)
            @RequestBody InventoryUpdateRequestDto inventoryUpdateRequestDto) {

        InventoryResponseDto inventoryResponseDto = inventoryService.updateInventory(inventoryId, inventoryUpdateRequestDto);
        return ResponseEntity.ok(inventoryResponseDto);
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "Delete inventory", description = "Deletes an inventory entry by product ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Inventory successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Inventory not found for the given product ID")
    })
    public ResponseEntity<Void> deleteInventory(
            @Parameter(description = "Product ID of the inventory to be deleted", required = true)
            @PathVariable String productId) {

        inventoryService.deleteInventory(productId);
        return ResponseEntity.noContent().build();
    }
}
