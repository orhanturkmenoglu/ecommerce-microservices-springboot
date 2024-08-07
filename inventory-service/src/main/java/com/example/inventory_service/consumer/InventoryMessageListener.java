package com.example.inventory_service.consumer;

import com.example.inventory_service.dto.InventoryRequestDto;
import com.example.inventory_service.dto.InventoryUpdateRequestDto;
import com.example.inventory_service.mapper.InventoryMapper;
import com.example.inventory_service.model.Inventory;
import com.example.inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryMessageListener {

    private final InventoryService inventoryService;
    private final InventoryMapper inventoryMapper;


    @RabbitListener(queues = {"${rabbit.mq.queue.name}"})
    public void receiveInventoryUpdateMessage(Inventory inventory) {
        log.info(String.format("RECEIVED MESSAGE -> %s", inventory.toString()));
        try {
            updateInventory(inventory);
        } catch (Exception e) {
            log.error("Error processing message", e);
            throw new AmqpRejectAndDontRequeueException("Error processing message", e);
        }
    }


    private void updateInventory(Inventory inventory) {
        InventoryUpdateRequestDto inventoryUpdateRequestDto = inventoryMapper.mapToInventoryUpdateRequestDto(inventory);
        log.info("InventoryMessageListener::updateInventory - inventoryRequestDto  : {}", inventoryUpdateRequestDto);
        inventoryService.updateInventory(inventory.getId(), inventoryUpdateRequestDto);
    }

}
