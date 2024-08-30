package com.example.customer_service.dto.addressDto;

import com.example.customer_service.enums.Country;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Data Transfer Object for creating an address")
public class AddressRequestDto implements Serializable {


    @Schema(description = "The country of the address", example = "TURKEY")
    private Country country = Country.TURKEY;

    @Schema(description = "The city of the address", example = "Istanbul")
    @NotEmpty(message = "Address city cannot be empty")
    private String city;

    @Schema(description = "The district of the address", example = "Kadikoy")
    @NotEmpty(message = "Address district cannot be empty")
    private String district;

    @Schema(description = "The street of the address", example = "Bagdat Caddesi")
    @NotEmpty(message = "Address street cannot be empty")
    private String street;

    @Schema(description = "The postal code of the address", example = "34700")
    @NotEmpty(message = "Address zipCode cannot be empty")
    private String zipCode;

    @Schema(description = "Additional description for the address", example = "Near the big park")
    private String description;

    @Schema(description = "The date and time when the address was created", example = "2024-08-30T10:00:00")
    private LocalDateTime createdDate;
}
