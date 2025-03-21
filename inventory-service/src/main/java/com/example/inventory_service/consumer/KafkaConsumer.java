package com.example.inventory_service.consumer;

import com.example.inventory_service.model.Inventory;
import com.example.inventory_service.service.InventoryService;
import com.example.inventory_service.dto.InventoryUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaConsumer {

    private final InventoryService inventoryService;

    @KafkaListener(topics = "inventory",
            groupId = "groupId")
    public void consumeProduct(@Payload Inventory inventory) {
        log.info("Consuming inventory update message: {}", inventory.toString());
        
        // Stok güncellemesi yap
        inventoryService.updateInventory(inventory.getId(), 
            InventoryUpdateRequestDto.builder()
                .productId(inventory.getProductId())
                .newQuantity(inventory.getStockQuantity())
                .build());
                
        log.info("Inventory updated successfully for ID: {}", inventory.getId());
    }
}
