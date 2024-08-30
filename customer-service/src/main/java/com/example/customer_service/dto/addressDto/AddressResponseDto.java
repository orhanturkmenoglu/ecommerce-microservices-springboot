package com.example.customer_service.dto.addressDto;

import com.example.customer_service.enums.Country;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Data Transfer Object for responding with address details")
public class AddressResponseDto {

    @Schema(description = "Unique identifier for the address", example = "12345")
    private String id;

    @Schema(description = "The country of the address", example = "TURKEY")
    private Country country = Country.TURKEY;

    @Schema(description = "The city of the address", example = "Istanbul")
    private String city;

    @Schema(description = "The district of the address", example = "Kadikoy")
    private String district;

    @Schema(description = "The street of the address", example = "Bagdat Caddesi")
    private String street;

    @Schema(description = "The postal code of the address", example = "34700")
    private String zipCode;

    @Schema(description = "Additional description for the address", example = "Near the big park")
    private String description;

    @Schema(description = "The date and time when the address was created", example = "2024-08-30T10:00:00")
    private LocalDateTime createdDate;
}
