package com.example.customer_service.controller;

import com.example.customer_service.dto.customerDto.CustomerRequestDto;
import com.example.customer_service.dto.customerDto.CustomerResponseDto;
import com.example.customer_service.dto.customerDto.CustomerUpdateRequestDto;
import com.example.customer_service.model.Address;
import com.example.customer_service.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;


    @PostMapping
    public ResponseEntity<CustomerResponseDto> createCustomer(@RequestBody CustomerRequestDto customerRequestDto) {
        CustomerResponseDto customer = customerService.createCustomer(customerRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(customer);
    }

    @GetMapping("/all")
    public ResponseEntity<List<CustomerResponseDto>> getCustomersAll() {
        List<CustomerResponseDto> customersAll = customerService.getCustomersAll();
        return ResponseEntity.ok(customersAll);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerResponseDto> getCustomerById(@PathVariable("customerId") String customerId) {
        CustomerResponseDto customer = customerService.getCustomerById(customerId);
        return ResponseEntity.ok(customer);
    }

    @GetMapping("/customerByFirstName")
    public  ResponseEntity<List<CustomerResponseDto>> getCustomersByFirstName(@RequestParam String firstName) {
        List<CustomerResponseDto> customersByFirstName = customerService.getCustomersByFirstName(firstName);
        return ResponseEntity.ok(customersByFirstName);
    }


    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> deleteCustomerById(@PathVariable("customerId") String customerId) {
        try {
            customerService.deleteCustomerById(customerId);
            return ResponseEntity.noContent().build();
        } catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<CustomerResponseDto> updateCustomer(
            @PathVariable String customerId,
            @RequestBody @Valid CustomerUpdateRequestDto customerUpdateRequestDto) {
        CustomerResponseDto updatedCustomer = customerService.updateCustomer(customerId, customerUpdateRequestDto);
        return ResponseEntity.ok(updatedCustomer);
    }
}
