package com.example.product_service.controller;

import com.example.product_service.dto.inventoryDto.InventoryRequestDto;
import com.example.product_service.dto.inventoryDto.InventoryResponseDto;
import com.example.product_service.dto.productDto.ProductRequestDto;
import com.example.product_service.dto.productDto.ProductResponseDto;
import com.example.product_service.dto.productDto.ProductUpdateRequestDto;
import com.example.product_service.enums.Category;
import com.example.product_service.model.Inventory;
import com.example.product_service.model.Product;
import com.example.product_service.service.ProductService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(ProductController.class)
public class ProductControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

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
    void createProduct_ShouldReturnProductResponseDto_WhenProductCreatedSuccessfully() throws Exception {
        when(productService.createProduct(productRequestDto)).thenReturn(productResponseDto);

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequestDto)))
                .andExpect(status().isCreated());
    }

    @Test
    void getProductsAll_ShouldReturnProductList_WhenProductsExist() throws Exception {
        when(productService.getProductsAll()).thenReturn(List.of(productResponseDto));

        mockMvc.perform(get("/api/v1/products/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void getProductById_ShouldReturnProductResponseDto_WhenProductFound() throws Exception {
        when(productService.getProductById(productRequestDto.getId())).thenReturn(productResponseDto);

        mockMvc.perform(get("/api/v1/products/{id}", productRequestDto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productResponseDto.getId()));
    }

    @Test
    void updateProduct_ShouldReturnUpdatedProduct_WhenProductUpdatedSuccessfully() throws Exception {
        when(productService.updateProductById(any(ProductUpdateRequestDto.class))).thenReturn(productResponseDto);

        mockMvc.perform(put("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productUpdateRequestDto)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteProduct_ShouldReturnSuccessMessage_WhenProductDeletedSuccessfully() throws Exception {
        mockMvc.perform(delete("/api/v1/products/{id}", productRequestDto.getId()))
                .andExpect(status().isOk());
    }
}
