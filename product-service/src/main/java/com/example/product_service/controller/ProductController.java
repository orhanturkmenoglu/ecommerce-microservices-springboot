package com.example.product_service.controller;

import com.example.product_service.dto.ProductRequestDto;
import com.example.product_service.dto.ProductResponseDto;
import com.example.product_service.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.ServiceUnavailableException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;


    int count = 1;
    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(@Valid
                                                            @RequestBody ProductRequestDto productRequestDto) {
        log.info("Retry Count : {}", count);
        count++;
        if (productRequestDto == null) {
            throw new IllegalArgumentException("Request body cannot be null");
        }
        ProductResponseDto createProduct = productService.createProduct(productRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createProduct);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProductResponseDto>> getProductsAll() throws ServiceUnavailableException {
        List<ProductResponseDto> productsAll = productService.getProductsAll();
        return ResponseEntity.ok(productsAll);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable String id) {
        ProductResponseDto productById = productService.getProductById(id);
        return ResponseEntity.ok(productById);
    }

    @GetMapping("/inventoryById/{inventoryId}")
    public ResponseEntity<ProductResponseDto> getInventoryById(@PathVariable String inventoryId) {
        ProductResponseDto productResponseDto = productService.getInventoryById(inventoryId);
        return ResponseEntity.ok(productResponseDto);
    }


    // http://localhost:8080/api/v1/products?minPrice=12&maxPrice=20;
    @GetMapping("/productByPriceRange")
    public ResponseEntity<List<ProductResponseDto>> getProductByPriceRange(@RequestParam("minPrice") double minPrice,
                                                                           @RequestParam("maxPrice") double maxPrice) {

        List<ProductResponseDto> productNamesByPriceRange = productService.getProductByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(productNamesByPriceRange);
    }

    @GetMapping("/productByQuantity")
    public ResponseEntity<List<ProductResponseDto>> getProductByQuantity(@RequestParam("quantity") int quantity) {
        List<ProductResponseDto> productNamesByQuantity = productService.getProductByQuantity(quantity);
        return ResponseEntity.ok(productNamesByQuantity);
    }

    @GetMapping("/productByPriceGreaterThanEqual")
    public ResponseEntity<List<ProductResponseDto>> getProductByPriceGreaterThanEqual(
            @RequestParam("price") double price) {
        List<ProductResponseDto> productNamesByPriceGreaterThan = productService.getProductByPriceGreaterThanEqual(price);
        return ResponseEntity.ok(productNamesByPriceGreaterThan);
    }

    @GetMapping("/productByPriceLessThanEqual")
    public ResponseEntity<List<ProductResponseDto>> getProductByPriceLessThanEqual(
            @RequestParam("price") double price) {

        List<ProductResponseDto> productByPriceLessThanEqual = productService.getProductByPriceLessThanEqual(price);
        return ResponseEntity.ok(productByPriceLessThanEqual);
    }

    @GetMapping("/productByCategory")
    public ResponseEntity<List<ProductResponseDto>> getProductByCategory(@RequestParam("category") String category) {
        List<ProductResponseDto> productByCategory = productService.getProductByCategory(category);
        return ResponseEntity.ok(productByCategory);
    }

    @PutMapping
    public ResponseEntity<ProductResponseDto> updateProductById(@RequestBody ProductRequestDto productRequestDto) {
        ProductResponseDto productById = productService.updateProductById(productRequestDto);
        return ResponseEntity.ok(productById);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProductById(@PathVariable String id) {
        return ResponseEntity.ok(productService.deleteProductById(id));
    }
}
