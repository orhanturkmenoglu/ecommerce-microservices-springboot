package com.example.product_service.service;

import com.example.product_service.dto.inventoryDto.InventoryRequestDto;
import com.example.product_service.dto.inventoryDto.InventoryResponseDto;
import com.example.product_service.dto.inventoryDto.InventoryUpdateRequestDto;
import com.example.product_service.dto.productDto.ProductRequestDto;
import com.example.product_service.dto.productDto.ProductResponseDto;
import com.example.product_service.dto.productDto.ProductUpdateRequestDto;
import com.example.product_service.enums.Category;
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
import org.apache.commons.io.FilenameUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.naming.ServiceUnavailableException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.aspectj.weaver.tools.cache.SimpleCacheFactory.path;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    private final InventoryClientService inventoryClientService;

    private final ProductMapper productMapper;

    private final String uploadDir = "uploads"; // proje kökünde olacak


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
            throw new NullPointerException("Product ID should not be null after saving.");
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
        saveProduct.setInventoryId(mapToInventory.getId());


        log.info("ProductService::createProduct - Updating inventory with inventory: {}", mapToInventory);
        log.info("ProductService::createProduct finished");
        return productMapper.mapToProductResponseDto(saveProduct);
    }

    // read
    @Cacheable(value = "products", key = "'all'")
    public List<ProductResponseDto> getProductsAll() throws ServiceUnavailableException {
        log.info("ProductService::getProductsAll started");

        List<Product> productList = getProductsList();

        log.info("ProductService::getProductsAll finished");
        return productMapper.mapToProductResponseDtoList(productList);
    }

    // getProductById
    @Cacheable(value = "products", key = "#productId")
    public ProductResponseDto getProductById(String productId) {
        log.info("ProductService::getProductById started");

        Product product = getProduct(productId);

        log.info("ProductService::getProductById finished");
        return productMapper.mapToProductResponseDto(product);
    }

    public String uploadProductImage(String productId, MultipartFile file) throws IOException {
        // 5MB sınırı
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("File size must be less than 5MB");
        }

        // Format kontrolü
        String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename()).toLowerCase();
        if (!fileExtension.equals("jpg") && !fileExtension.equals("jpeg") && !fileExtension.equals("png")) {
            throw new IllegalArgumentException("File format must be jpg, jpeg or png");
        }

        // Klasör oluştur (yoksa)
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Dosyayı kaydet
        String fileName = UUID.randomUUID().toString() + "." + fileExtension;
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Resim URL’si
        String imageUrl = "/images/" + fileName;

        // Ürünü güncelle
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Ürün bulunamadı"));
        product.setImageUrl(imageUrl);
        productRepository.save(product);

        return imageUrl;
    }
    // getInventoryById
    @Cacheable(value = "products", key = "#inventoryId")
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
    @CacheEvict(value = "products", key = "#productUpdateRequestDto.id",allEntries = true)
    public ProductResponseDto updateProductById(ProductUpdateRequestDto productUpdateRequestDto) {
        log.info("ProductService::updateProductById started");

        Product product = getProduct(productUpdateRequestDto.getId());
        log.info("ProductService::updateProductById - product {}", product);


        Product updatedProduct = getUpdatedProduct(productUpdateRequestDto, product);
        log.info("ProductService::updateProductById - updatedProduct {}", updatedProduct);

        // stok id kontrol et.
        inventoryClientService.getInventoryById(productUpdateRequestDto.getInventoryUpdateRequestDto().getInventoryId());

        // inventory-service stok takibini güncelle.
        Inventory inventory = updatedProduct.getInventory();
        inventory.setProductId(productUpdateRequestDto.getInventoryUpdateRequestDto().getProductId());
        inventory.setStockQuantity(productUpdateRequestDto.getInventoryUpdateRequestDto().getNewQuantity());
        InventoryUpdateRequestDto mapToInventoryUpdateRequestDto = productMapper.mapToInventoryUpdateRequestDto(inventory);

        log.info("ProductService::updateProductById - inventory {}", inventory);
        log.info("ProductService::updateProductById - mapToInventoryRequestDto {}", mapToInventoryUpdateRequestDto);


        inventoryClientService.updateInventory(updatedProduct.getInventoryId(),
                mapToInventoryUpdateRequestDto);

        log.info("ProductService::updateProductById finished");
        return productMapper.mapToProductResponseDto(updatedProduct);
    }


    // delete
    @CacheEvict(value = "products", key = "#productId",allEntries = true)
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
    @Cacheable(value = "productsByPriceRange", key = "'range:' + #minPrice + '-' + #maxPrice")
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


    @Cacheable(value = "productsByPrice", key = "'greaterOrEqual:' + #price")
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


    @Cacheable(value = "productsByPrice", key = "'lessOrEqual:' + #price")
    public List<ProductResponseDto> getProductByPriceLessThanEqual(double price) {
        log.info("ProductService::getProductNamesByPriceLessThanEqual started");

        List<Product> productList = getProductsList();


        log.info("ProductService::getProductNamesByPriceLessThanEqual finished");
        return productList.stream()
                .filter(product -> product.getPrice() <= price)
                .map(productMapper::mapToProductResponseDto)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "productsByQuantity", key = "#quantity")
    public List<ProductResponseDto> getProductByQuantity(int quantity) {
        log.info("ProductService::getProductByQuantity started");
        List<Product> productList = getProductsList();


        log.info("ProductService::getProductByQuantity finish");
        return productList.stream()
                .filter(product -> product.getInventory().getStockQuantity() == quantity)
                .map(productMapper::mapToProductResponseDto)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "productsByCategory", key = "#category")
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

    private Product getUpdatedProduct(ProductUpdateRequestDto productUpdateRequestDto, Product product) {
        product.setName(productUpdateRequestDto.getName());
        product.setDescription(productUpdateRequestDto.getDescription());
        product.setPrice(productUpdateRequestDto.getPrice());
        product.setCategory(productUpdateRequestDto.getCategory());
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
