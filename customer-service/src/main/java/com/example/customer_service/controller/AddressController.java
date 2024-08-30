package com.example.customer_service.controller;

import com.example.customer_service.dto.addressDto.AddressRequestDto;
import com.example.customer_service.dto.addressDto.AddressResponseDto;
import com.example.customer_service.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/address")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping("/all")
    @Operation(summary = "Get all addresses", description = "Retrieves a list of all addresses.")
    @ApiResponse(responseCode = "200", description = "List of addresses retrieved successfully")
    public ResponseEntity<List<AddressResponseDto>> getAddressAll() {
        List<AddressResponseDto> addressAll = addressService.getAddressAll();
        return ResponseEntity.ok(addressAll);
    }

    @GetMapping("/{addressId}")
    @Operation(summary = "Get address by ID", description = "Retrieves an address by its unique ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Address found"),
            @ApiResponse(responseCode = "404", description = "Address not found")
    })
    public ResponseEntity<AddressResponseDto> getAddressById(
            @PathVariable @Parameter(description = "Unique identifier of the address") String addressId) {
        AddressResponseDto address = addressService.getAddressById(addressId);
        return ResponseEntity.ok(address);
    }

    @DeleteMapping("/{addressId}")
    @Operation(summary = "Delete address by ID", description = "Deletes an address by its unique ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Address deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Address not found")
    })
    public ResponseEntity<Void> deleteAddressById(
            @PathVariable @Parameter(description = "Unique identifier of the address to delete") String addressId) {
        try {
            addressService.deleteAddressById(addressId);
            return ResponseEntity.noContent().build();
        } catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }

    @PutMapping("/{addressId}")
    @Operation(summary = "Update address", description = "Updates the details of an existing address.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Address updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Address not found")
    })
    public ResponseEntity<AddressResponseDto> updateAddress(
            @PathVariable @Parameter(description = "Unique identifier of the address to update") String addressId,
            @RequestBody @Valid @Schema(description = "Updated address details") AddressRequestDto addressRequestDto) {
        AddressResponseDto addressResponseDto = addressService.updateAddress(addressId, addressRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(addressResponseDto);
    }
}
