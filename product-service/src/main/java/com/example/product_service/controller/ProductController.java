package com.example.product_service.controller;

import com.example.product_service.dto.productDto.ProductRequestDto;
import com.example.product_service.dto.productDto.ProductResponseDto;
import com.example.product_service.dto.productDto.ProductUpdateRequestDto;
import com.example.product_service.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.naming.ServiceUnavailableException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Product Api", description = "Product management APIs")
public class ProductController {

    private final ProductService productService;

    private int count = 1;

    @PostMapping
    @Operation(summary = "Create a new product", description = "Creates a new product with the provided details.")
    public ResponseEntity<ProductResponseDto> createProduct(/*@Valid*/ @RequestBody ProductRequestDto productRequestDto) {
        log.info("Retry Count : {}", count);
        count++;
        if (productRequestDto == null) {
            throw new IllegalArgumentException("Request body cannot be null");
        }
        ProductResponseDto createProduct = productService.createProduct(productRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createProduct);
    }

    @PostMapping("/{productId}/image")
    public ResponseEntity<String> uploadProductImage(@PathVariable String productId,
                                                     @RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = productService.uploadProductImage(productId, file);
            return ResponseEntity.ok(imageUrl);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Resim yükleme işlemi başarısız oldu: " + e.getMessage());
        }
    }

    @GetMapping("/all")
    @Operation(summary = "Get all products", description = "Retrieves a list of all products.")
    @ApiResponse(responseCode = "200", description = "Successful operation",
            content = @Content(schema = @Schema(implementation = ProductResponseDto.class)))

    public ResponseEntity<List<ProductResponseDto>> getProductsAll() throws ServiceUnavailableException {
        List<ProductResponseDto> productsAll = productService.getProductsAll();
        return ResponseEntity.ok(productsAll);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a product by ID", description = "Retrieves a product by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product found",
                    content = @Content(schema = @Schema(implementation = ProductResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<ProductResponseDto> getProductById(
            @Parameter(description = "ID of the product to be retrieved") @PathVariable String id) {
        ProductResponseDto productById = productService.getProductById(id);
        return ResponseEntity.ok(productById);
    }


    @GetMapping("/inventoryById/{inventoryId}")
    @Operation(summary = "Get inventory by ID", description = "Retrieves product inventory by inventory ID.")
    @ApiResponse(responseCode = "200", description = "Successful operation",
            content = @Content(schema = @Schema(implementation = ProductResponseDto.class)))
    public ResponseEntity<ProductResponseDto> getInventoryById(
            @Parameter(description = "ID of the inventory to be retrieved") @PathVariable String inventoryId) {
        ProductResponseDto productResponseDto = productService.getInventoryById(inventoryId);
        return ResponseEntity.ok(productResponseDto);
    }


    // http://localhost:8080/api/v1/products?minPrice=12&maxPrice=20;
    @GetMapping("/productByPriceRange")
    @Operation(summary = "Get products by price range", description = "Retrieves products within a specified price range.")
    @ApiResponse(responseCode = "200", description = "Successful operation",
            content = @Content(schema = @Schema(implementation = ProductResponseDto.class)))
    public ResponseEntity<List<ProductResponseDto>> getProductByPriceRange(
            @Parameter(description = "Minimum price") @RequestParam("minPrice") double minPrice,
            @Parameter(description = "Maximum price") @RequestParam("maxPrice") double maxPrice) {
        List<ProductResponseDto> productNamesByPriceRange = productService.getProductByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(productNamesByPriceRange);
    }


    @GetMapping("/productByQuantity")
    @Operation(summary = "Get products by quantity", description = "Retrieves products by specified quantity.")
    @ApiResponse(responseCode = "200", description = "Successful operation",
            content = @Content(schema = @Schema(implementation = ProductResponseDto.class)))

    public ResponseEntity<List<ProductResponseDto>> getProductByQuantity(
            @Parameter(description = "Quantity of the product") @RequestParam("quantity") int quantity) {
        List<ProductResponseDto> productNamesByQuantity = productService.getProductByQuantity(quantity);
        return ResponseEntity.ok(productNamesByQuantity);
    }


    @GetMapping("/productByPriceGreaterThanEqual")
    @Operation(summary = "Get products by price greater than or equal to", description = "Retrieves products with price greater than or equal to specified value.")
    @ApiResponse(responseCode = "200", description = "Successful operation",
            content = @Content(schema = @Schema(implementation = ProductResponseDto.class)))

    public ResponseEntity<List<ProductResponseDto>> getProductByPriceGreaterThanEqual(@Parameter(description = "Price threshold")
                                                                                      @RequestParam("price") double price) {
        List<ProductResponseDto> productNamesByPriceGreaterThan = productService.getProductByPriceGreaterThanEqual(price);
        return ResponseEntity.ok(productNamesByPriceGreaterThan);
    }

    @GetMapping("/productByPriceLessThanEqual")
    @Operation(summary = "Get products by price less than or equal to", description = "Retrieves products with price less than or equal to specified value.")
    @ApiResponse(responseCode = "200", description = "Successful operation",
            content = @Content(schema = @Schema(implementation = ProductResponseDto.class)))
    public ResponseEntity<List<ProductResponseDto>> getProductByPriceLessThanEqual(@Parameter(description = "Price threshold")
                                                                                   @RequestParam("price") double price) {

        List<ProductResponseDto> productByPriceLessThanEqual = productService.getProductByPriceLessThanEqual(price);
        return ResponseEntity.ok(productByPriceLessThanEqual);
    }


    @GetMapping("/productByCategory")
    @Operation(summary = "Get products by category", description = "Retrieves products by specified category.")
    @ApiResponse(responseCode = "200", description = "Successful operation",
            content = @Content(schema = @Schema(implementation = ProductResponseDto.class)))
    public ResponseEntity<List<ProductResponseDto>> getProductByCategory(@Parameter(description = "Category of the products")
                                                                         @RequestParam("category") String category) {
        List<ProductResponseDto> productByCategory = productService.getProductByCategory(category);
        return ResponseEntity.ok(productByCategory);
    }


    @PutMapping
    @Operation(summary = "Update product by ID", description = "Updates a product's details by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated successfully",
                    content = @Content(schema = @Schema(implementation = ProductResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<ProductResponseDto> updateProductById(@RequestBody
                                                                ProductUpdateRequestDto productUpdateRequestDto) {
        ProductResponseDto productById = productService.updateProductById(productUpdateRequestDto);
        return ResponseEntity.ok(productById);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product by ID", description = "Deletes a product by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<String> deleteProductById(@Parameter(description = "ID of the product to be deleted")
                                                    @PathVariable String id) {
        return ResponseEntity.ok(productService.deleteProductById(id));
    }
}
