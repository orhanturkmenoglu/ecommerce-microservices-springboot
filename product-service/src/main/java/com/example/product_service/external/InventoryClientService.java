package com.example.product_service.external;


import com.example.product_service.model.Inventory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "INVENTORY-SERVICE")
public interface InventoryClientService {


    @PostMapping("/api/v1/inventories/create")
    Inventory addInventory(@RequestBody Inventory inventory);

    @GetMapping("/api/v1/inventories/{productId}")
    Inventory getInventoryByProductId(@PathVariable("productId") String productId);

    @PutMapping("/api/v1/inventories/{inventoryId}")
    Inventory updateInventory(@PathVariable("inventoryId") String inventoryId,Inventory inventory);

    @DeleteMapping("/api/v1/inventories/{productId}")
    void deleteInventory(@PathVariable("productId") String productId);

    @GetMapping("/api/v1/inventories/getInventoryId/{id}")
    Inventory getInventoryById(@PathVariable String id);

}
