package com.example.product_service.service;

import com.example.product_service.dto.inventoryDto.InventoryRequestDto;
import com.example.product_service.dto.inventoryDto.InventoryResponseDto;
import com.example.product_service.dto.productDto.ProductRequestDto;
import com.example.product_service.dto.productDto.ProductResponseDto;
import com.example.product_service.enums.Category;
import com.example.product_service.exception.InventoryNotFoundException;
import com.example.product_service.external.InventoryClientService;
import com.example.product_service.mapper.ProductMapper;
import com.example.product_service.model.Inventory;
import com.example.product_service.model.Product;
import com.example.product_service.repository.ProductRepository;
import com.example.product_service.util.ProductMessage;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.ServiceUnavailableException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    private final InventoryClientService inventoryClientService;

    private final ProductMapper productMapper;

    // create
    @Transactional
    @CircuitBreaker(name = "inventoryServiceBreaker", fallbackMethod = "inventoryServiceFallback")
    @Retry(name = "inventoryServiceBreaker", fallbackMethod = "inventoryServiceFallback")
    @RateLimiter(name = "createProductLimiter", fallbackMethod = "inventoryServiceFallback")
    public ProductResponseDto createProduct(ProductRequestDto productRequestDto) {
        log.info("ProductService::createProduct started");

        Product product = productMapper.mapToProduct(productRequestDto);
        log.info("ProductService::createProduct - Saving product: {}", productRequestDto);
        Product saveProduct = productRepository.save(product);


        if (saveProduct.getId() == null) {
            log.error("ProductService::createProduct - Product ID is null after saving.");
            throw new IllegalStateException("Product ID should not be null after saving.");
        }

        log.info("ProductService::createProduct - Creating inventory for product id: {} , product: {}",
                saveProduct.getId(), saveProduct);

        InventoryRequestDto inventoryRequestDto = productMapper.mapToInventoryRequestDto(saveProduct.getInventory());
        inventoryRequestDto.setProductId(saveProduct.getId());

        log.info("ProductService::createProduct - save inventoryRequestDto  : {}", inventoryRequestDto);
        log.info("ProductService::createProduct - save inventoryRequestDto with product id : {}", inventoryRequestDto.getProductId());

        // create inventory
        InventoryResponseDto inventoryResponseDto = inventoryClientService.addInventory(inventoryRequestDto);
        log.info("ProductService::createProduct - Updating inventory with inventory: {}", inventoryResponseDto.toString());

        Inventory mapToInventory = productMapper.mapToInventory(inventoryResponseDto);
        log.info("ProductService::createProduct - Updating inventory with inventory: {}", mapToInventory.toString());
        saveProduct.setInventory(mapToInventory);
        saveProduct.setInventoryId(inventoryResponseDto.getId());


        log.info("ProductService::createProduct - Updating inventory with inventory: {}", mapToInventory);
        log.info("ProductService::createProduct finished");
        return productMapper.mapToProductResponseDto(saveProduct);
    }


    // read
    public List<ProductResponseDto> getProductsAll() throws ServiceUnavailableException {
        log.info("ProductService::getProductsAll started");

        if (LocalDateTime.now().getHour() == 17) {
            throw new ServiceUnavailableException(ProductMessage.SERVICE_UNAVAILABLE_EXCEPTION);
        }

        List<Product> productList = getProductsList();

        log.info("ProductService::getProductsAll finished");
        return productMapper.mapToProductResponseDtoList(productList);
    }

    // getProductById
    public ProductResponseDto getProductById(String productId) {
        log.info("ProductService::getProductById started");

        Product product = getProduct(productId);

        log.info("ProductService::getProductById finished");
        return productMapper.mapToProductResponseDto(product);
    }

    // getInventoryById
    public ProductResponseDto getInventoryById(String inventoryId) {
        log.info("ProductService::getInventoryById started");
        Product product = productRepository.findByInventoryId(inventoryId).orElseThrow(()
                -> new InventoryNotFoundException(ProductMessage.INVENTORY_NOT_FOUND + inventoryId));

        log.info("ProductService::getInventoryById finished");
        return productMapper.mapToProductResponseDto(product);
    }

    // update
    @CircuitBreaker(name = "inventoryServiceBreaker", fallbackMethod = "inventoryServiceFallback")
    @Retry(name = "inventoryServiceBreaker", fallbackMethod = "inventoryServiceFallback")
    @RateLimiter(name = "createProductLimiter", fallbackMethod = "inventoryServiceFallback")
    public ProductResponseDto updateProductById(ProductRequestDto productRequestDto) {
        log.info("ProductService::updateProductById started");

        Product product = getProduct(productRequestDto.getId());
        log.info("ProductService::updateProductById - product {}", product);

        Product updatedProduct = getUpdatedProduct(productRequestDto, product);
        log.info("ProductService::updateProductById - updatedProduct {}", updatedProduct);


        // inventory-service stok takibini güncelle.
        Inventory inventory = updatedProduct.getInventory();
        InventoryRequestDto mapToInventoryRequestDto = productMapper.mapToInventoryRequestDto(inventory);

        log.info("ProductService::updateProductById - inventory {}", inventory);
        log.info("ProductService::updateProductById - mapToInventoryRequestDto {}", mapToInventoryRequestDto);


        inventoryClientService.updateInventory(updatedProduct.getInventoryId(),
                mapToInventoryRequestDto);

        log.info("ProductService::updateProductById finished");
        return productMapper.mapToProductResponseDto(updatedProduct);
    }


    // delete
    public String deleteProductById(String productId) {
        log.info("ProductService::deleteProductById started");

        Product product = getProduct(productId);

        getInventoryById(product.getInventoryId());

        productRepository.deleteById(product.getId());

        // ürün silindiği zaman stok takibide silinmelidir.
        inventoryClientService.deleteInventory(product.getId());

        log.info("ProductService::deleteProductById finished");
        return "successful deleted ";
    }


    // Get product names by price range using stream api
    public List<ProductResponseDto> getProductByPriceRange(double minPrice, double maxPrice) {
        log.info("ProductService::getProductNamesByPriceRange started");

        List<Product> productList = getProductsList();


        log.info("ProductService::getProductNamesByPriceRange finished");
        return productList.stream()
                .filter(product -> product.getPrice() >= minPrice && product.getPrice() <= maxPrice)
                .map(productMapper::mapToProductResponseDto)
                .sorted(Collections.reverseOrder())
                .collect(Collectors.toList());
    }

    public List<ProductResponseDto> getProductByPriceGreaterThanEqual(double price) {
        log.info("ProductService::getProductNamesByPriceGreaterThanEqual started");


        List<Product> productList = getProductsList();


        log.info("ProductService::getProductNamesByPriceGreaterThanEqual finished");
        return productList.stream()
                .filter(product -> product.getPrice() >= price)
                .map(productMapper::mapToProductResponseDto)
                .sorted()
                .collect(Collectors.toList());
    }


    public List<ProductResponseDto> getProductByPriceLessThanEqual(double price) {
        log.info("ProductService::getProductNamesByPriceLessThanEqual started");

        List<Product> productList = getProductsList();


        log.info("ProductService::getProductNamesByPriceLessThanEqual finished");
        return productList.stream()
                .filter(product -> product.getPrice() <= price)
                .map(productMapper::mapToProductResponseDto)
                .collect(Collectors.toList());
    }

    public List<ProductResponseDto> getProductByQuantity(int quantity) {
        log.info("ProductService::getProductByQuantity started");
        List<Product> productList = getProductsList();


        log.info("ProductService::getProductByQuantity finish");
        return productList.stream()
                .filter(product -> product.getInventory().getStockQuantity() == quantity)
                .map(productMapper::mapToProductResponseDto)
                .collect(Collectors.toList());
    }

    public List<ProductResponseDto> getProductByCategory(String category) {
        log.info("ProductService::getProductByCategory started");

        Category categoryTypeEnum = null;
        try{
            categoryTypeEnum = Category.valueOf(category.toUpperCase());
            log.info("ProductResponseDto::getProductByCategory - categoryType : {}", categoryTypeEnum);
        }catch (IllegalArgumentException exception)
        {
            log.error("Product category type  provided: {}",category);
            // Hata durumunda uygun bir işlem yapabilirsiniz, örneğin özel bir istisna fırlatma
            throw new IllegalArgumentException("Product category type: " + categoryTypeEnum);
        }
        List<Product> productList = productRepository.findByCategory(categoryTypeEnum);

        log.info("ProductService::getProductByCategory finish");
        return productList.stream()
                .map(productMapper::mapToProductResponseDto)
                .collect(Collectors.toList());
    }

    private List<Product> getProductsList() {
        return productRepository.findAll();
    }


    private Product getProduct(String productId) {
        return productRepository.findById(productId).orElseThrow(() ->
                new NullPointerException(ProductMessage.PRODUCT_NOT_FOUND + productId));
    }

    private Product getUpdatedProduct(ProductRequestDto productRequestDto, Product product) {
        product.setId(productRequestDto.getId());
        product.setName(productRequestDto.getName());
        product.setDescription(productRequestDto.getDescription());
        product.setPrice(productRequestDto.getPrice());
        product.setCategory(productRequestDto.getCategory());
        product.setInventoryId(productRequestDto.getInventoryRequestDto().getId());
        return productRepository.save(product);
    }

    private ProductResponseDto inventoryServiceFallback(Exception exception) {
        log.info("fallback is executed because servise is down :{}", exception.getMessage());
        return ProductResponseDto.builder()
                .name("IPHONE 13")
                .description("This product is created as a fallback because some service is down")
                .build();
    }

}
