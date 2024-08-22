package com.example.customer_service.dto.addressDto;

import com.example.customer_service.enums.Country;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressResponseDto {

    private String id;
    private Country country = Country.TURKEY;
    private String city;
    private String district;
    private String street;
    private String zipCode;
    private String description;
    private LocalDateTime createdDate;
}
