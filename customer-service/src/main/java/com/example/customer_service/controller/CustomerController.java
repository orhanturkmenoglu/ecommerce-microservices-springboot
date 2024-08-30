package com.example.customer_service.controller;

import com.example.customer_service.dto.customerDto.CustomerRequestDto;
import com.example.customer_service.dto.customerDto.CustomerResponseDto;
import com.example.customer_service.dto.customerDto.CustomerUpdateRequestDto;
import com.example.customer_service.external.CargoClientService;
import com.example.customer_service.model.Cargo;
import com.example.customer_service.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

    private final CargoClientService cargoClientService;

    @PostMapping
    @Operation(summary = "Create a new customer", description = "Creates a new customer with the provided details.")
    @ApiResponse(responseCode = "201", description = "Customer created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    public ResponseEntity<CustomerResponseDto> createCustomer(
            @RequestBody @Schema(description = "Customer details for the new customer") CustomerRequestDto customerRequestDto) {
        CustomerResponseDto customer = customerService.createCustomer(customerRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(customer);
    }


    @GetMapping("/all")
    @Operation(summary = "Get all customers", description = "Retrieves a list of all customers.")
    @ApiResponse(responseCode = "200", description = "List of customers")
    public ResponseEntity<List<CustomerResponseDto>> getCustomersAll() {
        List<CustomerResponseDto> customersAll = customerService.getCustomersAll();
        return ResponseEntity.ok(customersAll);
    }

    @GetMapping("/{customerId}")
    @Operation(summary = "Get customer by ID", description = "Retrieves a customer by their unique ID.")
    @ApiResponse(responseCode = "200", description = "Customer found")
    @ApiResponse(responseCode = "404", description = "Customer not found")
    public ResponseEntity<CustomerResponseDto> getCustomerById(
            @PathVariable @Parameter(description = "Unique identifier of the customer") String customerId) {
        CustomerResponseDto customer = customerService.getCustomerById(customerId);
        return ResponseEntity.ok(customer);
    }

    @GetMapping("/customerByFirstName")
    @Operation(summary = "Get customers by first name", description = "Retrieves a list of customers with the specified first name.")
    @ApiResponse(responseCode = "200", description = "List of customers with the given first name")
    public ResponseEntity<List<CustomerResponseDto>> getCustomersByFirstName(
            @RequestParam @Parameter(description = "First name of the customers") String firstName) {
        List<CustomerResponseDto> customersByFirstName = customerService.getCustomersByFirstName(firstName);
        return ResponseEntity.ok(customersByFirstName);
    }

    @DeleteMapping("/{customerId}")
    @Operation(summary = "Delete customer by ID", description = "Deletes a customer by their unique ID.")
    @ApiResponse(responseCode = "204", description = "Customer deleted successfully")
    @ApiResponse(responseCode = "404", description = "Customer not found")
    public ResponseEntity<Void> deleteCustomerById(
            @PathVariable @Parameter(description = "Unique identifier of the customer to delete") String customerId) {
        try {
            customerService.deleteCustomerById(customerId);
            return ResponseEntity.noContent().build();
        } catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }

    @PutMapping("/{customerId}")
    @Operation(summary = "Update customer details", description = "Updates the details of an existing customer.")
    @ApiResponse(responseCode = "200", description = "Customer updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @ApiResponse(responseCode = "404", description = "Customer not found")
    public ResponseEntity<CustomerResponseDto> updateCustomer(
            @PathVariable @Parameter(description = "Unique identifier of the customer to update") String customerId,
            @RequestBody @Valid @Schema(description = "Updated customer details") CustomerUpdateRequestDto customerUpdateRequestDto) {
        CustomerResponseDto updatedCustomer = customerService.updateCustomer(customerId, customerUpdateRequestDto);
        return ResponseEntity.ok(updatedCustomer);
    }

    @GetMapping("/track-cargo/{trackingNumber}")
    @Operation(summary = "Get cargo by tracking number", description = "Retrieves cargo details using the tracking number.")
    @ApiResponse(responseCode = "200", description = "Cargo details retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Cargo not found")
    public ResponseEntity<Cargo> getCargoByTrackingNumber(
            @PathVariable @Parameter(description = "Tracking number of the cargo") String trackingNumber) {
        Cargo cargo = cargoClientService.getCargoByTrackingNumber(trackingNumber);
        return ResponseEntity.ok(cargo);
    }
}
