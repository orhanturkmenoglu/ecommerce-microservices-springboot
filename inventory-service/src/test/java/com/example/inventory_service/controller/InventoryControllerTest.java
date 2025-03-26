package com.example.inventory_service.controller;

import com.example.inventory_service.dto.InventoryRequestDto;
import com.example.inventory_service.dto.InventoryResponseDto;
import com.example.inventory_service.dto.InventoryUpdateRequestDto;
import com.example.inventory_service.model.Inventory;
import com.example.inventory_service.service.InventoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(InventoryController.class)
@ExtendWith(MockitoExtension.class)
public class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private InventoryService inventoryService;

    private Inventory inventory;
    private InventoryResponseDto inventoryResponseDto;
    private InventoryRequestDto inventoryRequestDto;
    private InventoryUpdateRequestDto inventoryUpdateRequestDto;

    @BeforeEach
    public void setUp() {

        inventory = new Inventory();
        inventory.setId("INV123");
        inventory.setProductId("PRD123");
        inventory.setStockQuantity(10);
        inventory.setLastUpdated(LocalDateTime.now());


        inventoryRequestDto = new InventoryRequestDto();
        inventoryRequestDto.setId(inventory.getId());
        inventoryRequestDto.setProductId(inventory.getProductId());
        inventoryRequestDto.setStockQuantity(inventory.getStockQuantity());

        inventoryUpdateRequestDto = new InventoryUpdateRequestDto();
        inventoryUpdateRequestDto.setInventoryId(inventory.getId());
        inventoryUpdateRequestDto.setProductId(inventory.getProductId());
        inventoryUpdateRequestDto.setNewQuantity(inventory.getStockQuantity());
        inventoryUpdateRequestDto.setLastUpdated(inventory.getLastUpdated());

        inventoryResponseDto = new InventoryResponseDto();
        inventoryResponseDto.setId(inventory.getId());
        inventoryResponseDto.setProductId(inventory.getProductId());
        inventoryResponseDto.setStockQuantity(inventory.getStockQuantity());
        inventoryResponseDto.setLastUpdated(inventory.getLastUpdated());

    }


    @Test
    public void addInventory_ShouldReturnInventoryResponseDto_WhenValidRequest() throws Exception {
        when(inventoryService.addInventory(any(InventoryRequestDto.class))).thenReturn(inventoryResponseDto);

        mockMvc.perform(post("/api/v1/inventories/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inventoryRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(inventoryResponseDto.getId()))
                .andExpect(jsonPath("$.productId").value(inventoryResponseDto.getProductId()))
                .andExpect(jsonPath("$.stockQuantity").value(inventoryResponseDto.getStockQuantity()));
    }

    @Test
    public void getAllInventories_ShouldReturnListOfInventories_WhenInventoriesExist() throws Exception {
        when(inventoryService.getAllInventories()).thenReturn(List.of(inventoryResponseDto));

        mockMvc.perform(get("/api/v1/inventories/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(inventoryResponseDto.getId()));
    }

    @Test
    public void getInventoryByProductId_ShouldReturnInventoryResponseDto_WhenInventoryByProductIdExists() throws Exception {
        when(inventoryService.getInventoryByProductId(inventory.getProductId())).thenReturn(inventoryResponseDto);

        mockMvc.perform(get("/api/v1/inventories/{productId}", inventory.getProductId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(inventoryResponseDto.getId()))
                .andExpect(jsonPath("$.productId").value(inventoryResponseDto.getProductId()));
    }

    @Test
    public void getInventoryById_ShouldReturnInventoryResponseDto_WhenInventoryByIdExists() throws Exception {
        when(inventoryService.getInventoryById(inventory.getId())).thenReturn(inventoryResponseDto);

        mockMvc.perform(get("/api/v1/inventories/getInventoryId/{id}", inventory.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(inventoryResponseDto.getId()))
                .andExpect(jsonPath("$.productId").value(inventoryResponseDto.getProductId()));
    }

    @Test
    public void updateInventory_ShouldReturnInventoryResponseDto_WhenInventoryUpdatedSuccessfully() throws Exception {
        when(inventoryService.updateInventory(any(String.class), any(InventoryUpdateRequestDto.class)))
                .thenReturn(inventoryResponseDto);

        mockMvc.perform(put("/api/v1/inventories/{inventoryId}", inventory.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inventoryUpdateRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockQuantity").value(inventoryResponseDto.getStockQuantity()));
    }

    @Test
    public void deleteInventory_ShouldReturnNoContent_WhenInventoryDeletedSuccessfully() throws Exception {
        mockMvc.perform(delete("/api/v1/inventories/{productId}", inventory.getProductId()))
                .andExpect(status().isNoContent());
    }
}
