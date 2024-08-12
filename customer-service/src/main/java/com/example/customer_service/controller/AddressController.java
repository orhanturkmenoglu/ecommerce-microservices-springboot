package com.example.customer_service.controller;

import com.example.customer_service.dto.addressDto.AddressRequestDto;
import com.example.customer_service.dto.addressDto.AddressResponseDto;
import com.example.customer_service.service.AddressService;
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

    @PostMapping
    public ResponseEntity<AddressResponseDto> createAddress(@RequestBody AddressRequestDto addressRequestDto) {
        AddressResponseDto address = addressService.createAddress(addressRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(address);
    }

    @GetMapping("/all")
    public ResponseEntity<List<AddressResponseDto>> getAddressAll() {
        List<AddressResponseDto> addressAll = addressService.getAddressAll();
        return ResponseEntity.ok(addressAll);
    }

    @GetMapping("/{addressId}")
    public ResponseEntity<AddressResponseDto> getAddressById(@PathVariable("addressId") String addressId) {
        AddressResponseDto address = addressService.getAddressById(addressId);
        return ResponseEntity.ok(address);
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<AddressResponseDto> deleteAddressById(@PathVariable("addressId") String addressId) {
        try {
            addressService.deleteAddressById(addressId);
            return ResponseEntity.noContent().build();
        } catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<AddressResponseDto> updateAddress(@PathVariable("addressId") String addressId,
                                                            @Valid @RequestBody AddressRequestDto addressRequestDto) {
        AddressResponseDto addressResponseDto = addressService.updateAddress(addressId, addressRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(addressResponseDto);
    }
}
