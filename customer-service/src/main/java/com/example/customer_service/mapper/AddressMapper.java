package com.example.customer_service.mapper;

import com.example.customer_service.dto.addressDto.AddressRequestDto;
import com.example.customer_service.dto.addressDto.AddressResponseDto;
import com.example.customer_service.model.Address;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AddressMapper {
    public Address mapToAddress(AddressRequestDto addressRequestDto) {
        return Address.builder()
                .country(addressRequestDto.getCountry())
                .city(addressRequestDto.getCity())
                .district(addressRequestDto.getDistrict())
                .street(addressRequestDto.getStreet())
                .zipCode(addressRequestDto.getZipCode())
                .description(addressRequestDto.getDescription())
                .build();
    }

    public Address mapToAddress(AddressResponseDto addressResponseDto) {
        return Address.builder()
                .id(addressResponseDto.getId())
                .country(addressResponseDto.getCountry())
                .city(addressResponseDto.getCity())
                .district(addressResponseDto.getDistrict())
                .street(addressResponseDto.getStreet())
                .zipCode(addressResponseDto.getZipCode())
                .description(addressResponseDto.getDescription())
                .build();
    }

    public AddressResponseDto mapToAddressResponseDto(Address address) {
        return AddressResponseDto.builder()
                .id(address.getId())
                .country(address.getCountry())
                .city(address.getCity())
                .district(address.getDistrict())
                .street(address.getStreet())
                .zipCode(address.getZipCode())
                .description(address.getDescription())
                .build();
    }


    public List<AddressResponseDto> mapToAddressResponseDtoList(List<Address> addressList) {
        return addressList.stream()
                .map(this::mapToAddressResponseDto)
                .collect(Collectors.toList());
    }
}
