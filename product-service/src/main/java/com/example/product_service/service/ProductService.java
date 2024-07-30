package com.example.product_service.service;

import com.example.product_service.dto.ProductRequestDto;
import com.example.product_service.dto.ProductResponseDto;
import com.example.product_service.exception.InventoryNotFoundException;
import com.example.product_service.exception.ProductNotFoundException;
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
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final InventoryClientService inventoryClientService;
    private final ProductMapper productMapper;

    // create
    // ürün kaydederken inventory-service ile iletişime geçip stok miktarını kayıt altına alacaz.
    @Transactional
    @CircuitBreaker(name = "inventoryServiceBreaker", fallbackMethod = "inventoryServiceFallback")
    @Retry(name = "inventoryServiceBreaker", fallbackMethod = "inventoryServiceFallback")  // tekrar deneme mekanizması.
    @RateLimiter(name = "createProductLimiter", fallbackMethod = "inventoryServiceFallback")
    // rate limiter (oran sınırlayıcı) kullanarak belirli bir süre içinde belirli sayıda istek kabul edebilirsiniz
    public ProductResponseDto createProduct(ProductRequestDto productRequestDto) {
        log.info("ProductService::createProduct started");

        Product product = productMapper.mapToProduct(productRequestDto);

        Product saveProduct = productRepository.save(product);


        if (saveProduct.getId() == null) {
            throw new IllegalStateException("Product ID should not be null after saving.");
        }

        Inventory inventory = inventoryClientService.addInventory(saveProduct.getInventory());
        saveProduct.setInventoryId(inventory.getId());

        inventory.setProductId(saveProduct.getId());

        inventoryClientService.updateInventory(inventory.getId(), inventory);

        log.info("ProductService::createProduct finished");
        return productMapper.mapToProductResponseDto(saveProduct);
    }

    public ProductResponseDto inventoryServiceFallback(Exception exception) {
        log.info("fallback is executed because servise is down :{}", exception.getMessage());
        return ProductResponseDto.builder()
                .name("IPHONE 13")
                .description("This is product is created IPHONE 13 because some service is down")
                .build();
    }

    // read
    public List<ProductResponseDto> getProductsAll() throws ServiceUnavailableException {
        log.info("ProductService::getProductsAll started");

        if (LocalDateTime.now().getHour() == 17) {
            throw new ServiceUnavailableException(ProductMessage.SERVICE_UNAVAILABLE_EXCEPTION);
        }

        List<Product> productList = productRepository.findAll();

        log.info("ProductService::getProductsAll finished");
        return productMapper.mapToProductResponseDtoList(productList);
    }

    // getById
    // ürün kimliği ile stok bilgilerini getir.
    public ProductResponseDto getProductById(String productId) {
        log.info("ProductService::getProductById started");
        Product product = productRepository.findById(productId).orElseThrow(()
                -> new ProductNotFoundException(ProductMessage.PRODUCT_NOT_FOUND + productId));

        log.info("ProductService::getProductById finished");
        return productMapper.mapToProductResponseDto(product);
    }

    // getById
    // stok kimliği ile stok bilgilerini getir.
    public ProductResponseDto getInventoryById(String inventoryId) {
        log.info("ProductService::getInventoryById started");
        Product product = productRepository.findByInventoryId(inventoryId).orElseThrow(()
                -> new InventoryNotFoundException(ProductMessage.INVENTORY_NOT_FOUND + inventoryId));

        log.info("ProductService::getInventoryById finished");
        return productMapper.mapToProductResponseDto(product);
    }

    // update
    public ProductResponseDto updateProductById(ProductRequestDto productRequestDto) {
        log.info("ProductService::updateProductById started");
        Product product = productRepository.findById(productRequestDto.getId()).orElseThrow(() ->
                new NullPointerException(ProductMessage.PRODUCT_NOT_FOUND + productRequestDto.getId()));

        Product updatedProduct = getUpdatedProduct(productRequestDto, product);

        // inventory-service stok takibini güncelle.

        inventoryClientService.updateInventory(updatedProduct.getInventoryId(),
                updatedProduct.getInventory());

        log.info("ProductService::updateProductById finished");
        return productMapper.mapToProductResponseDto(updatedProduct);
    }


    // delete

    public String deleteProductById(String productId) {
        log.info("ProductService::deleteProductById started");

        ProductResponseDto product = getProductById(productId);

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

        List<Product> productList = productRepository.findAll();


        log.info("ProductService::getProductNamesByPriceRange finished");
        return productList.stream()
                .filter(product -> product.getPrice() >= minPrice && product.getPrice() <= maxPrice)
                .map(productMapper::mapToProductResponseDto)
                .sorted(Collections.reverseOrder())
                .collect(Collectors.toList());
    }

    // Get product names by price range using stream api

    public List<ProductResponseDto> getProductByPriceGreaterThanEqual(double price) {
        log.info("ProductService::getProductNamesByPriceGreaterThanEqual started");

        List<Product> productList = productRepository.findAll();


        log.info("ProductService::getProductNamesByPriceGreaterThanEqual finished");
        return productList.stream()
                .filter(product -> product.getPrice() >= price)
                .map(productMapper::mapToProductResponseDto)
                .sorted()
                .collect(Collectors.toList());
    }


    public List<ProductResponseDto> getProductByPriceLessThanEqual(double price) {
        log.info("ProductService::getProductNamesByPriceLessThanEqual started");

        List<Product> productList = productRepository.findAll();


        log.info("ProductService::getProductNamesByPriceLessThanEqual finished");
        return productList.stream()
                .filter(product -> product.getPrice() <= price)
                .map(productMapper::mapToProductResponseDto)
                .collect(Collectors.toList());
    }


    public List<ProductResponseDto> getProductByQuantity(int quantity) {
        log.info("ProductService::getProductByQuantity started");
        List<Product> productList = productRepository.findAll();


        log.info("ProductService::getProductByQuantity finish");
        return productList.stream()
                .filter(product -> product.getInventory().getStockQuantity() == quantity)
                .map(productMapper::mapToProductResponseDto)
                .collect(Collectors.toList());
    }

    public List<ProductResponseDto> getProductByCategory(String category) {
        log.info("ProductService::getProductByCategory started");
        List<Product> productList = productRepository.findAll();


        log.info("ProductService::getProductByCategory finish");
        return productList.stream()
                .filter(product -> Objects.deepEquals(product.getCategory(), category))
                .map(productMapper::mapToProductResponseDto)
                .collect(Collectors.toList());
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

}
