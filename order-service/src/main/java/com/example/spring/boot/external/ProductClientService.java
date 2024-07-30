package com.example.spring.boot.external;

import com.example.spring.boot.model.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "PRODUCT-SERVICE")
public interface ProductClientService {
    @PostMapping("/api/v1/products")
    Product addProduct(@RequestBody Product product);

    @GetMapping("/api/v1/products/{id}")
    Product getProductById(@PathVariable("id") String id);

    @PutMapping("/api/v1/products/{id}")
    Product updateProduct(@PathVariable("id") String id, @RequestBody Product product);

    @DeleteMapping("/api/v1/products/{id}")
    void deleteProduct(@PathVariable("id") String id);
}
