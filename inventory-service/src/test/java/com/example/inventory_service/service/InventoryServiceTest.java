package com.example.inventory_service.service;

import com.example.inventory_service.dto.InventoryRequestDto;
import com.example.inventory_service.dto.InventoryResponseDto;
import com.example.inventory_service.dto.InventoryUpdateRequestDto;
import com.example.inventory_service.exception.InventoryNotFoundException;
import com.example.inventory_service.exception.ProductNotFoundException;
import com.example.inventory_service.mapper.InventoryMapper;
import com.example.inventory_service.model.Inventory;
import com.example.inventory_service.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private InventoryService inventoryService;

    @Mock
    private InventoryMapper inventoryMapper;

    private Inventory inventory;
    private InventoryRequestDto inventoryRequestDto;
    private InventoryUpdateRequestDto inventoryUpdateRequestDto;
    private InventoryResponseDto inventoryResponseDto;

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
    public void addInventory_ShouldReturnInventoryResponseDto_WhenInventoryRequestDtoIsValid() {

        // Arrange
        when(inventoryMapper.mapToInventory(inventoryRequestDto)).thenReturn(inventory);
        when(inventoryRepository.save(inventory)).thenReturn(inventory);
        when(inventoryMapper.mapToInventoryResponseDto(inventory)).thenReturn(inventoryResponseDto);

        // Actual
        InventoryResponseDto inventory = inventoryService.addInventory(inventoryRequestDto);

        // Assert
        assert inventory != null;
        assert inventoryResponseDto != null;
        assert inventory.getId().equals(inventoryResponseDto.getId());
        assert inventory.getProductId().equals(inventoryResponseDto.getProductId());
        assert Objects.equals(inventory.getStockQuantity(), inventoryResponseDto.getStockQuantity());
        assert Objects.equals(inventory.getLastUpdated(), inventoryResponseDto.getLastUpdated());
    }

    @Test
    public void addInventory_ShouldThrowException_WhenInventoryRequestDtoIsInvalid() {

        inventory = null;
        when(inventoryMapper.mapToInventory(inventoryRequestDto)).thenReturn(inventory);

        // Actual
        NullPointerException exception =
                assertThrows(NullPointerException.class, () -> {
                    inventoryService.addInventory(inventoryRequestDto);
                });

        // Assert

        assert exception != null;
        assert exception.getMessage().equals("Inventory cannot be null");

    }

    @Test
    public void getAllInventories_ShouldReturnListOfInventoryResponseDto_WhenInventoriesArePresent() {
        // Arrange
        when(inventoryRepository.findAll()).thenReturn(List.of(inventory));
        when(inventoryMapper.mapToInventoryResponseDtoList(List.of(inventory))).thenReturn(List.of(inventoryResponseDto));
        // Actual
        List<InventoryResponseDto> inventories = inventoryService.getAllInventories();

        // Assert
        assert inventories != null;
        assert inventories.size() == 1;
        assert inventories.get(0).getId().equals(inventoryResponseDto.getId());
        assert inventories.get(0).getProductId().equals(inventoryResponseDto.getProductId());
        assert Objects.equals(inventories.get(0).getStockQuantity(), inventoryResponseDto.getStockQuantity());
        assert Objects.equals(inventories.get(0).getLastUpdated(), inventoryResponseDto.getLastUpdated());
    }

    @Test
    public void getAllInventories_ShouldThrowException_WhenInventoriesAreNotPresent() {

        // Arrange
        when(inventoryRepository.findAll()).thenReturn(List.of());

        // Actual
        NullPointerException exception =
                assertThrows(NullPointerException.class, () -> {
                    inventoryService.getAllInventories();
                });

        // Assert
        assert exception != null;
        assert exception.getMessage().equals("Inventories not found");
    }

    @Test
    public void getInventoryById_ShouldReturnInventoryResponseDto_WhenInventoryExists() {
        // Arrange
        when(inventoryRepository.findById(inventory.getId())).thenReturn(Optional.of(inventory));
        when(inventoryMapper.mapToInventoryResponseDto(inventory)).thenReturn(inventoryResponseDto);

        // Actual
        InventoryResponseDto response = inventoryService.getInventoryById(inventory.getId());


        // Assert
        assert response != null;
        assert response.getId().equals(inventoryResponseDto.getId());
        assert response.getProductId().equals(inventoryResponseDto.getProductId());
        assert Objects.equals(response.getStockQuantity(), inventoryResponseDto.getStockQuantity());
        assert Objects.equals(response.getLastUpdated(), inventoryResponseDto.getLastUpdated());

    }

    @Test
    public void getInventoryByProductId_ShouldReturnInventoryResponseDto_WhenInventoryProductExists() {

        // Arrange
        when(inventoryRepository.findByProductId(inventory.getProductId())).thenReturn(Optional.of(inventory));
        when(inventoryMapper.mapToInventoryResponseDto(inventory)).thenReturn(inventoryResponseDto);

        // Actual
        InventoryResponseDto response = inventoryService.getInventoryByProductId(inventory.getProductId());

        // Assert
        assert response != null;
        assert response.getId().equals(inventoryResponseDto.getId());
        assert response.getProductId().equals(inventoryResponseDto.getProductId());
        assert Objects.equals(response.getStockQuantity(), inventoryResponseDto.getStockQuantity());
        assert Objects.equals(response.getLastUpdated(), inventoryResponseDto.getLastUpdated());

    }

    @Test
    public void getInventoryByProductId_ShouldThrowException_WhenInventoryProductNotExists() {

        // Arrange
        when(inventoryRepository.findByProductId(inventory.getProductId())).thenReturn(Optional.empty());

        // Actual
        ProductNotFoundException exception =
                assertThrows(ProductNotFoundException.class, () -> {
                    inventoryService.getInventoryByProductId(inventory.getProductId());
                });

        // Assert
        assert exception != null;
        assert exception.getMessage().equals("Inventory not found for product ID: " + inventory.getProductId());
    }

    @Test
    public void getInventoryById_ShouldReturnInventoryResponseDto_WhenInventoryDoesNotExists()
    {
        // Arrange
        when(inventoryRepository.findById(inventory.getId())).thenReturn(Optional.empty());

        // Actual
        NullPointerException exception =
                assertThrows(NullPointerException.class, () -> {
                    inventoryService.getInventoryById(inventory.getId());
                });

        // Assert
        assert exception != null;
        assert exception.getMessage().equals("Inventory not found for inventory ID: " + inventory.getId());
    }


    @Test
    public void updateInventory_ShouldReturnInventoryResponseDto_WhenInventoryUpdateRequestDtoIsValid() {

        // Arrange
        when(inventoryRepository.findById(inventory.getId())).thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(inventory)).thenReturn(inventory);
        when(inventoryMapper.mapToInventoryUpdateRequestDto(inventory)).thenReturn(inventoryUpdateRequestDto);

        // Actual
        InventoryResponseDto response = inventoryService.updateInventory(inventory.getId(), inventoryUpdateRequestDto);

        // Assert
        assert response != null;
        assert response.getId().equals(inventoryResponseDto.getId());
        assert response.getProductId().equals(inventoryResponseDto.getProductId());
    }

    @Test
    public void deleteInventory_ShouldReturnInventoryResponseDto_WhenInventoryExists() {

        // Arrange
        when(inventoryRepository.findById(inventory.getId())).thenReturn(Optional.of(inventory));

        // Actual
         inventoryService.deleteInventory(inventory.getId());

        // verify
        verify(inventoryRepository, times(1)).delete(inventory);

    }

    @Test
    void deleteInventory_ShouldThrowProductNotFoundException_WhenProductIdIsNull() {
        // Arrange
        String productId = null; // Test için null değer

        // Act & Assert
        ProductNotFoundException exception = assertThrows(ProductNotFoundException.class, () -> {
            inventoryService.deleteInventory(productId);
        });

        // Beklenen hata mesajını dinamik olarak oluşturuyoruz
        String expectedMessage = "Inventory not found for product ID: " + productId;
        assertEquals(expectedMessage, exception.getMessage());

        // Repository ve Mapper çağrılmamalı!
        verify(inventoryRepository, never()).delete(any());
    }

    @Test
    void getInventory_ShouldThrowNullPointerException_WhenInventoryIdNotFound() throws Exception {
        // Arrange
        String inventoryId = "123";
        when(inventoryRepository.findById(inventoryId)).thenReturn(Optional.empty());

        // Reflection ile private metodu alıyoruz
        Method getInventoryMethod = InventoryService.class.getDeclaredMethod("getInventory", String.class);
        getInventoryMethod.setAccessible(true); // Private metodu erişilebilir hale getiriyoruz

        // Act & Assert
        InvocationTargetException exception = assertThrows(InvocationTargetException.class, () -> {
            getInventoryMethod.invoke(inventoryService, inventoryId); // Reflection ile metodu çağırıyoruz
        });

        assertEquals("Inventory not found for inventory ID: " + inventoryId, exception.getCause().getMessage());

        // Doğrulama: Repository findById çağrılmış olmalı
        verify(inventoryRepository, times(1)).findById(inventoryId);
    }
}
