package com.example.payment_service.external;

import com.example.payment_service.model.Inventory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "INVENTORY-SERVICE")
public interface InventoryServiceClient {

    @GetMapping("/api/v1/inventories/getInventoryId/{id}")
    Inventory getInventoryById(@PathVariable String id);
}
