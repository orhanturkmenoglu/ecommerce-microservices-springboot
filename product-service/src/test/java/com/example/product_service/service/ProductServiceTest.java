package com.example.product_service.service;

import com.example.product_service.dto.inventoryDto.InventoryRequestDto;
import com.example.product_service.dto.inventoryDto.InventoryResponseDto;
import com.example.product_service.dto.productDto.ProductRequestDto;
import com.example.product_service.dto.productDto.ProductResponseDto;
import com.example.product_service.dto.productDto.ProductUpdateRequestDto;
import com.example.product_service.enums.Category;
import com.example.product_service.external.InventoryClientService;
import com.example.product_service.mapper.ProductMapper;
import com.example.product_service.model.Inventory;
import com.example.product_service.model.Product;
import com.example.product_service.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.naming.ServiceUnavailableException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Mock
    private InventoryClientService inventoryClientService;

    @Mock
    private ProductMapper productMapper;

    private Product product;
    private ProductRequestDto productRequestDto;
    private ProductUpdateRequestDto productUpdateRequestDto;
    private ProductResponseDto productResponseDto;
    private Inventory inventory;
    private InventoryRequestDto inventoryRequestDto;
    private InventoryResponseDto inventoryResponseDto;

    @BeforeEach
    public void setUp() {

        product = new Product();
        product.setId("PROD123");
        product.setName("IPHONE 13");
        product.setDescription("256GB STAR IŞIĞI");
        product.setPrice(42000);
        product.setCategory(Category.ELEKTRONİK);
        product.setCreatedDate(LocalDateTime.now());

        productRequestDto = new ProductRequestDto();
        productRequestDto.setId(product.getId());
        productRequestDto.setName(product.getName());
        productRequestDto.setDescription(product.getDescription());
        productRequestDto.setPrice(product.getPrice());
        productRequestDto.setCategory(product.getCategory());

        productUpdateRequestDto = new ProductUpdateRequestDto();
        productUpdateRequestDto.setId(product.getId());
        productUpdateRequestDto.setName(product.getName());
        productUpdateRequestDto.setDescription(product.getDescription());
        productUpdateRequestDto.setPrice(product.getPrice());

        inventory = new Inventory();
        inventory.setId("INV123");
        inventory.setProductId(product.getId());
        inventory.setStockQuantity(10);

        inventoryRequestDto = new InventoryRequestDto();
        inventoryRequestDto.setId(inventory.getId());
        inventoryRequestDto.setProductId(inventory.getProductId());
        inventoryRequestDto.setStockQuantity(inventory.getStockQuantity());

        inventoryResponseDto = new InventoryResponseDto();
        inventoryResponseDto.setId(inventory.getId());
        inventoryResponseDto.setProductId(inventory.getProductId());
        inventoryResponseDto.setStockQuantity(inventory.getStockQuantity());

        product.setInventoryId(inventory.getId());
        product.setInventory(inventory);

        productResponseDto = new ProductResponseDto();
        productResponseDto.setId(product.getId());
        productResponseDto.setName(product.getName());
        productResponseDto.setDescription(product.getDescription());
        productResponseDto.setPrice(product.getPrice());
        productResponseDto.setCategory(product.getCategory());
        productResponseDto.setInventoryId(product.getInventoryId());
        productResponseDto.setInventoryResponseDto(inventoryResponseDto);
        productResponseDto.setCreatedDate(product.getCreatedDate());

    }

    @Test
    public void createProduct_ShouldReturnProductResponseDto_WhenValidRequest() {

        // Arrange
        when(productMapper.mapToProduct(productRequestDto)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.mapToInventoryRequestDto(product.getInventory())).thenReturn(inventoryRequestDto);
        when(inventoryClientService.addInventory(inventoryRequestDto)).thenReturn(inventoryResponseDto);
        when(productMapper.mapToInventory(inventoryResponseDto)).thenReturn(inventory);
        when(productMapper.mapToProductResponseDto(product)).thenReturn(productResponseDto);

        // Actual
        ProductResponseDto result = productService.createProduct(productRequestDto);

        // Assert
        assert result != null;
        assert result.getId().equals(productResponseDto.getId());
        assert result.getName().equals(productResponseDto.getName());
        assert result.getDescription().equals(productResponseDto.getDescription());
        assert Objects.equals(result.getPrice(), productResponseDto.getPrice());
        assert result.getCategory().equals(productResponseDto.getCategory());
        assert result.getInventoryId().equals(productResponseDto.getInventoryId());
        assert result.getInventoryResponseDto().getId().equals(productResponseDto.getInventoryResponseDto().getId());
        assert result.getInventoryResponseDto().getProductId().equals(productResponseDto.getInventoryResponseDto().getProductId());
        assert Objects.equals(result.getInventoryResponseDto().getStockQuantity(), productResponseDto.getInventoryResponseDto().getStockQuantity());
        assert result.getCreatedDate().equals(productResponseDto.getCreatedDate());

    }

    @Test
    public void createProduct_ShouldThrowNullPointerException_WhenProductRequestDtoIsNull() {

        product.setId(null);
        // Arrange
        when(productMapper.mapToProduct(productRequestDto)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);

        // Actual
        NullPointerException exception = assertThrows(NullPointerException.class,()->{
            productService.createProduct(productRequestDto);
        });

        // Assert
        assert exception != null;
        assertEquals("Product ID should not be null after saving.", exception.getMessage());

        // Verify
        verify(productMapper, times(1)).mapToProduct(productRequestDto);
        verify(productRepository, times(1)).save(product);

    }

    @Test
    public void getProductsAll_ReturnsListProductResponseDto_WhenProductsExist() throws ServiceUnavailableException {

        // Arrange
        when(productRepository.findAll()).thenReturn(Collections.singletonList(product));
        when(productMapper.mapToProductResponseDtoList(Collections.singletonList(product))).thenReturn(Collections.singletonList(productResponseDto));

        // Actual
        List<ProductResponseDto> productsAll = productService.getProductsAll();

        // Assert
        assert productsAll != null;
        assert productsAll.size() == 1;
        assert productsAll.get(0).getId().equals(productResponseDto.getId());
        assert productsAll.get(0).getName().equals(productResponseDto.getName());
        assert productsAll.get(0).getDescription().equals(productResponseDto.getDescription());
        assert Objects.equals(productsAll.get(0).getPrice(), productResponseDto.getPrice());
        assert productsAll.get(0).getCategory().equals(productResponseDto.getCategory());
        assert productsAll.get(0).getInventoryId().equals(productResponseDto.getInventoryId());
        assert productsAll.get(0).getInventoryResponseDto().getId().equals(productResponseDto.getInventoryResponseDto().getId());
        assert productsAll.get(0).getInventoryResponseDto().getProductId().equals(productResponseDto.getInventoryResponseDto().getProductId());
        assert Objects.equals(productsAll.get(0).getInventoryResponseDto().getStockQuantity(), productResponseDto.getInventoryResponseDto().getStockQuantity());
        assert productsAll.get(0).getCreatedDate().equals(productResponseDto.getCreatedDate());

        // verify
        verify(productRepository, times(1)).findAll();
        verify(productMapper, times(1)).mapToProductResponseDtoList(Collections.singletonList(product));

    }

    @Test
    public void getProductsAll_ReturnsEmptyList_WhenProductsDoNotExist() throws ServiceUnavailableException {

        // Arrange
        when(productRepository.findAll()).thenReturn(Collections.emptyList());
        when(productMapper.mapToProductResponseDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());


        // Actual
        List<ProductResponseDto> productsAll = productService.getProductsAll();

        // Assert
        assert productsAll != null;
        assert productsAll.isEmpty();

        // verify
        verify(productRepository, times(1)).findAll();
        verify(productMapper, times(1)).mapToProductResponseDtoList(Collections.emptyList());

    }

    @Test
    public void getProductById_ReturnsProductResponseDto_WhenProductExists() throws ServiceUnavailableException {

        // Arrange
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(productMapper.mapToProductResponseDto(product)).thenReturn(productResponseDto);


        // Actual
        ProductResponseDto productById = productService.getProductById(product.getId());

        // Assert
        assert productById != null;
        assert productById.getId().equals(productResponseDto.getId());
        assert productById.getName().equals(productResponseDto.getName());
        assert productById.getDescription().equals(productResponseDto.getDescription());
        assert Objects.equals(productById.getPrice(), productResponseDto.getPrice());
        assert productById.getCategory().equals(productResponseDto.getCategory());
        assert productById.getInventoryId().equals(productResponseDto.getInventoryId());
        assert productById.getInventoryResponseDto().getId().equals(productResponseDto.getInventoryResponseDto().getId());
        assert productById.getInventoryResponseDto().getProductId().equals(productResponseDto.getInventoryResponseDto().getProductId());
        assert Objects.equals(productById.getInventoryResponseDto().getStockQuantity(), productResponseDto.getInventoryResponseDto().getStockQuantity());
        assert productById.getCreatedDate().equals(productResponseDto.getCreatedDate());

        // Verify
        verify(productRepository, times(1)).findById(product.getId());
        verify(productMapper, times(1)).mapToProductResponseDto(product);

    }

    @Test
    public void getProductById_ShouldThrowNullPointerException_WhenProductDoesNotExist() throws ServiceUnavailableException {

        product.setId(null);
        // Arrange
        when(productRepository.findById(product.getId())).thenReturn(Optional.empty());


        // Actual
        NullPointerException exception = assertThrows(NullPointerException.class,()->{
            productService.getProductById(product.getId());
        });

        // Assert
        assert exception != null;
        assertEquals("PRODUCT NOT FOUND WITH ID :   null", exception.getMessage());
    }



}
